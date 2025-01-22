import Form from '@rjsf/bootstrap-4';
import bs3Form from '@rjsf/core';
import validator from '@rjsf/validator-ajv6';
import parse from 'html-react-parser';
import debounce from 'lodash.debounce';
import React from 'react';
import * as ReactDOM from 'react-dom/client';
import applyNavs from 'react-jsonschema-form-pagination';
import applyBs4Navs from 'react-jsonschema-form-pagination-bs4';
import * as HtmlHelper from './HtmlHelperGPT';

class ReactFormElement extends HTMLElement {
  constructor() {
    super();
    // 1) Setup shadow DOM & React root
    this.mountPoint = document.createElement('div');
    this.root = ReactDOM.createRoot(this.mountPoint);
    this.attachShadow({ mode: 'open' }).appendChild(this.mountPoint);

    // 2) Copy attributes (excluding "id") -> this.attrs
    this.attrs = Object.fromEntries(
      this.getAttributeNames()
        .filter((n) => n !== 'id')
        .map((n) => [n, this.getAttribute(n)])
    );

    // 3) Merge in global props-var (if any)
    this.props ||= {};
    if (this.attrs['props-var']) {
      const globalProps = window[this.attrs['props-var']] || {};
      this.props = { ...this.props, ...globalProps };
    }

    // 4) Prepare state with default onSubmit if none
    this.state = {};
    this.state.onSubmit ||= ({ formData }, e) => e.target?.submit?.();

    // 5) Possibly override with AJAX onSubmit
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
      headers: { 'Content-Type': 'application/json' },
      body,
    })
      .then((response) => {
        if (response.ok) {
          // Handle HX-Trigger header
          const htmxTriggerHeader = response.headers.get('HX-Trigger');
          if (htmxTriggerHeader && typeof htmx !== 'undefined') {
            this.processHtmxTrigger(htmxTriggerHeader);
          }
        }

        return response.text();
      })
      .then((html) => {
        const targetEl = document.getElementById(targetId);
        if (targetEl) HtmlHelper.setInnerHTML(targetEl, html);
      })
      .catch((e) => console.error('Error in doAjax:', e));
  }

  /**
   * Function to process the HX-Trigger header and trigger corresponding HTMX actions
   * @param {string} headerValue - The value of the HX-Trigger header
   */
  processHtmxTrigger(headerValue) {
    let triggers;

    try {
      // Attempt to parse the header as JSON
      triggers = JSON.parse(headerValue);
    } catch (e) {
      // If not JSON, assume it's a comma-separated list of event names
      triggers = headerValue.split(',').map(event => event.trim());
    }

    // Normalize triggers to an array of [event, data] pairs
    let triggerPairs = [];

    if (typeof triggers === 'string') {
      triggerPairs.push([triggers, {}]);
    } else if (Array.isArray(triggers)) {
      triggers.forEach(event => triggerPairs.push([event, {}]));
    } else if (typeof triggers === 'object') {
      for (const [event, data] of Object.entries(triggers)) {
        triggerPairs.push([event, data]);
      }
    }

    // Iterate over each trigger pair and activate HTMX actions
    triggerPairs.forEach(([event, data]) => {
      // Select elements with hx-trigger attribute containing the eventName
      // Using ~= to match whole words in space-separated list
      const selector = `[hx-trigger~="${event}"]`;
      const elements = document.querySelectorAll(selector);

      elements.forEach(element => {
        // Make the element visible if it was hidden
        if (element.style.display === 'none') {
          element.style.display = 'inline-block';
        }

        // Trigger the HTMX event on the element with additional data
        htmx.trigger(element, event, { detail: data });
      });
    });
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

    // Check single form-dataset-url (schema+uiSchema+formData in one)
    const datasetUrl = this.attrs['form-dataset-url'];
    if (datasetUrl) {
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
      // Possibly fetch each piece individually
      const schemaUrl = this.attrs['schema-url'];
      if (schemaUrl) {
        try {
          const resp = await fetch(schemaUrl);
          if (!resp.ok) {
            throw new Error(`HTTP error! status: ${resp.status}`);
          }
          schema = await resp.json();
        } catch (e) {
          console.error('Error fetching schema:', e);
        }
      }

      const uiSchemaUrl = this.attrs['ui-schema-url'];
      if (uiSchemaUrl) {
        try {
          const resp = await fetch(uiSchemaUrl);
          if (!resp.ok) {
            throw new Error(`HTTP error! status: ${resp.status}`);
          }
          uiSchema = await resp.json();
        } catch (e) {
          console.error('Error fetching uiSchema:', e);
        }
      }

      const formDataUrl = this.attrs['form-data-url'];
      if (formDataUrl) {
        try {
          const resp = await fetch(formDataUrl);
          if (!resp.ok) {
            throw new Error(`HTTP error! status: ${resp.status}`);
          }
          formData = await resp.json();
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

    // Choose bootstrap 3 or 4
    const FormWithPagination =
      this.attrs.theme === 'bs3'
        ? applyNavs(bs3Form)
        : applyBs4Navs(Form);

    // For saveonly mode
    const saveonlyEnabled = !!this.attrs.saveonly;
    let tempFormData = data.formData || {};

    // Debounce storing updated formData
    const delayedUpdate = debounce(
      (newData) => {
        tempFormData = newData;
      },
      300 // ms
    );

    // Watch changes (only if saveonly)
    const handleChange = (e) => {
      if (saveonlyEnabled) {
        delayedUpdate(e.formData);
      }
    };

    // Actual "Save" action
    const handleSave = () => {
      if (!saveonlyEnabled) return;
      const ajaxTarget = this.attrs['ajax-target'];
      if (!ajaxTarget) return;

      const method = (this.attrs.method || 'get').toLowerCase();
      const actionUrl = this.attrs.action;

      if (!actionUrl) {
        console.warn('No action URL for saveOnly submission.');
        return;
      }

      if (method === 'get') {
        const connector = actionUrl.includes('?') ? '&' : '?';
        const fullUrl = actionUrl + connector + new URLSearchParams(tempFormData);
        this.doAjax(fullUrl, method, null, ajaxTarget);
      } else {
        const jsonBody = JSON.stringify(tempFormData);
        this.doAjax(actionUrl, method, jsonBody, ajaxTarget);
      }
    };

    // Possibly render a "Save" button
    let saveButton = null;
    if (saveonlyEnabled) {
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

    // Render the React form
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
      .catch((e) => console.error('Failed to mount React form:', e));
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
      const globalProps = window[newVal] || {};
      this.props = { ...this.props, ...globalProps };
      this.mount();
    }
  }
}

customElements.define('react-form', ReactFormElement);