import Form from '@rjsf/bootstrap-4';
import bs3Form from '@rjsf/core';
import validator from '@rjsf/validator-ajv6';
import parse from 'html-react-parser';
import React from 'react';
import * as ReactDOM from 'react-dom/client';
import applyNavs from "react-jsonschema-form-pagination";
import applyBs4Navs from "react-jsonschema-form-pagination-bs4";
import * as HtmlHelper from './HtmlHelperGPT';
// Removed axios import

class ReactFormElement extends HTMLElement {
  constructor() {
    super();

    // 1) Setup shadow DOM & React root
    this.mountPoint = document.createElement('div');
    this.root = ReactDOM.createRoot(this.mountPoint);
    this.attachShadow({ mode: 'open' }).appendChild(this.mountPoint);

    // 2) Copy all tag attributes (except "id") to this.attrs
    this.attrs = {};
    for (const name of this.getAttributeNames()) {
      const value = this.getAttribute(name);
      this.attrs[name] = value;
    }
    delete this.attrs.id; // We don't want to conflict with others

    // 3) Setup props (if there's a global variable name in props-var)
    this.props ||= {};
    if (this.attrs['props-var']) {
      this.props = {
        ...this.props,
        ...window[this.attrs['props-var']] // merges global variable
      };
    }

    // 4) Setup a default onSubmit in case none is provided
    this.state = {};
    this.state.onSubmit ||= ({ formData }, e) => e.target?.submit?.();

    // 5) Possibly override the onSubmit with AJAX logic
    this.configAjaxOnSubmit();
  }

  /**
   * If the user specifies 'ajax-target', we do an AJAX call to that endpoint
   * instead of standard form submission.
   */
  configAjaxOnSubmit() {
    const ajaxTarget = this.attrs['ajax-target'];
    if (!ajaxTarget) return;

    const method = (this.attrs.method || 'get').toLowerCase();
    const actionUrl = this.attrs.action;

    // Reusable onSubmit logic for GET/POST/PUT/etc.
    this.state.onSubmit = ({ formData }) => {
      if (!actionUrl) {
        console.warn('No action URL set for AJAX submission.');
        return;
      }

      if (method === 'get') {
        // Build URL with query params
        const connector = actionUrl.includes('?') ? '&' : '?';
        const fullUrl = actionUrl + connector + new URLSearchParams(formData);

        this.doAjax(fullUrl, method, null, ajaxTarget);
      } else {
        // POST, PUT, DELETE, etc.
        const jsonBody = JSON.stringify(formData);
        this.doAjax(actionUrl, method, jsonBody, ajaxTarget);
      }
    };
  }

  /**
   * Generic helper to do fetch calls
   * and place the result (HTML) inside a target element's innerHTML.
   */
  doAjax(url, method, body, targetId) {
    fetch(url, {
      method,
      headers: new Headers({ 'Content-Type': 'application/json' }),
      body: body,
    })
      .then(res => res.text())
      .then(html => {
        const targetEl = document.getElementById(targetId);
        if (targetEl) HtmlHelper.setInnerHTML(targetEl, html);
      })
      .catch(e => console.error('Error in doAjax:', e));
  }

  /**
   * Retrieve JSON data from either:
   *   1) inline props,
   *   2) combined "form-dataset-url" resource,
   *   3) or separate "schema-url", "ui-schema-url", "form-data-url".
   */
  async retrieveJson() {
    // Start with inline props
    let { schema, uiSchema, formData } = this.props;

    const datasetUrl = this.attrs['form-dataset-url'];
    if (datasetUrl) {
      // Single endpoint with {schema, uiSchema, formData}
      try {
        const response = await fetch(datasetUrl);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        schema = data.schema;
        uiSchema = data.uiSchema;
        formData = data.formData;
      } catch (e) {
        console.error('Error fetching form-dataset:', e);
      }
    } else {
      // Possibly fetch separate schema, uiSchema, formData
      const schemaUrl = this.attrs['schema-url'];
      if (schemaUrl) {
        try {
          const response = await fetch(schemaUrl);
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          schema = await response.json();
        } catch (e) {
          console.error('Error fetching schema:', e);
        }
      }

      const uiSchemaUrl = this.attrs['ui-schema-url'];
      if (uiSchemaUrl) {
        try {
          const response = await fetch(uiSchemaUrl);
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          uiSchema = await response.json();
        } catch (e) {
          console.error('Error fetching uiSchema:', e);
        }
      }

      const formDataUrl = this.attrs['form-data-url'];
      if (formDataUrl) {
        try {
          const response = await fetch(formDataUrl);
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          formData = await response.json();
        } catch (e) {
          console.error('Error fetching formData:', e);
        }
      }
    }

    return { schema, uiSchema, formData };
  }

  /**
   * Called after retrieving JSON to actually render the form.
   */
  createForm(data) {
    if (!data?.schema) {
      console.warn('No schema found, cannot render form.');
      return;
    }

    // Decide on bootstrap 3 vs 4 form
    const FormWithPagination = this.attrs.theme === 'bs3'
      ? applyNavs(bs3Form)
      : applyBs4Navs(Form);

    const isSaveOnly = !!this.attrs.saveonly;
    let tempFormData = {};

    // Track changes if saveonly is used
    const handleChange = (e) => {
      if (isSaveOnly) {
        tempFormData = e.formData;
      }
    };

    const handleSave = () => {
      if (!isSaveOnly || !this.attrs['ajax-target']) return;
      const method = (this.attrs.method || 'get').toLowerCase();
      const actionUrl = this.attrs.action;

      if (!actionUrl) {
        console.warn('No action URL for saveOnly submission.');
        return;
      }

      if (method === 'get') {
        const connector = actionUrl.includes('?') ? '&' : '?';
        const fullUrl = actionUrl + connector + new URLSearchParams(tempFormData);
        this.doAjax(fullUrl, method, null, this.attrs['ajax-target']);
      } else {
        const jsonBody = JSON.stringify(tempFormData);
        this.doAjax(actionUrl, method, jsonBody, this.attrs['ajax-target']);
      }
    };

    // Build an optional Save button
    let saveButton;
    if (isSaveOnly) {
      const btnText = this.attrs.saveonlybtntxt || 'Save';
      saveButton = (
        <button className="btn btn-warning" type="button" onClick={handleSave}>
          {btnText}
        </button>
      );
    }

    // Decide on default CSS
    let contextPath = window.contextPath || '/';
    if (!contextPath.endsWith('/')) contextPath += '/';

    const defaultBs3Css = `${contextPath}react-form/css/bootswatch-3.4.1-cosmo.css`;
    const defaultBs4Css = `${contextPath}react-form/css/bootswatch-4.6.2-litera.css`;

    // Render
    this.root.render(
      <React.Fragment>
        {/* 
          Load a stylesheet, either from 'cssHref' or from a default bootswatch
          depending on the theme attribute.
        */}
        <link
          rel="stylesheet"
          href={
            this.attrs.cssHref
              ? this.attrs.cssHref
              : this.attrs.theme === 'bs3'
                ? defaultBs3Css
                : defaultBs4Css
          }
        />

        <FormWithPagination
          {...this.attrs}
          {...this.props}
          schema={data.schema}
          uiSchema={data.uiSchema}
          formData={data.formData}
          onChange={handleChange}
          onSubmit={this.state.onSubmit}
          validator={validator}
        >
          {/* 
            If there are child elements (slot content in the HTML),
            parse them as HTML and insert them.
          */}
          {this.children?.length > 0 && parse(this.innerHTML)}

          {saveButton}
        </FormWithPagination>
      </React.Fragment>
    );
  }

  /**
   * Orchestrate retrieveJson -> createForm
   */
  mount() {
    this.retrieveJson()
      .then((data) => this.createForm(data))
      .catch(e => console.error('Failed to mount React form:', e));
  }

  unmount() {
    this.root.unmount();
  }

  // Lifecycle callbacks
  connectedCallback() {
    this.mount();
  }
  disconnectedCallback() {
    this.unmount();
  }

  // If the user changes 'props-var' attribute dynamically, we re-load
  static get observedAttributes() {
    return ['props-var'];
  }
  attributeChangedCallback(attrName, oldVal, newVal) {
    if (attrName === 'props-var') {
      this.props = { ...this.props, ...window[newVal] };
      this.mount();
    }
  }
}

customElements.define('react-form', ReactFormElement);