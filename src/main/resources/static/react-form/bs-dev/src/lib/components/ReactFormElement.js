import validator from '@rjsf/validator-ajv6';
import parse from 'html-react-parser';
import React from 'react';
import * as ReactDOM from 'react-dom/client';
// import Form from '@rjsf/core';
import Form from '@rjsf/bootstrap-4';
// import { StyledEngineProvider } from '@mui/material/styles';
// import Form from '@rjsf/mui';
import applyNavs from "react-jsonschema-form-pagination";

class ReactFormElement extends HTMLElement {
  constructor() {
    super();

    this.mountPoint = document.createElement('div');
    this.root = ReactDOM.createRoot(this.mountPoint);
    this.attachShadow({ mode: 'open' }).appendChild(this.mountPoint);

    // Copys all tag attrs but id
    this.attrs = {};
    for (const name of this.getAttributeNames()) {
      const value = this.getAttribute(name);
      this.attrs[name] = value;
    }
    delete this.attrs.id;

    // Copys all self defined tag props to mimic this.props
    this.props ||= {};
    if (this.attrs['props-var']) {
      this.props = {
        ...this.props,
        ...window['props-var']
      }
    }

    // Creates and mimics this.state
    this.state = {};
    this.state.onSubmit ||= ({ formData }, e) => e.target.submit();
    this.configAjaxOnSubmit();
  }

  configAjaxOnSubmit() {
    if (this.attrs['ajax-target']) {
      const tagId = this.attrs['ajax-target'];
      const method = (this.attrs.method || 'get').toLowerCase();

      switch (method) {
        case 'get':
          this.state.onSubmit = ({ formData }) => {
            let url = this.attrs.action;
            url += url.includes('?') ? '&' : '?';

            fetch(url + new URLSearchParams(formData), {
              method: method,
              headers: new Headers({
                'Content-Type': 'application/json'
              })
            })
              .then(res => res.text())
              .then(data => this.setInnerHTML(document.getElementById(tagId), data))
              .catch(e => console.error(e));
          }
          break;
        default:
          this.state.onSubmit = ({ formData }) => {
            fetch(this.attrs.action, {
              method: method,
              body: JSON.stringify(formData),
              headers: new Headers({
                'Content-Type': 'application/json'
              })
            })
              .then(res => res.text())
              .then(data => this.setInnerHTML(document.getElementById(tagId), data))
              .catch(e => console.error(e));
          }
      }
    }
  }

  setInnerHTML(elm, html) {
    elm.innerHTML = html;
    Array.from(elm.querySelectorAll('script')).forEach(oldScript => {
      const newScript = document.createElement('script');
      Array.from(oldScript.attributes)
        .forEach(attr => newScript.setAttribute(attr.name, attr.value));
      newScript.appendChild(document.createTextNode(oldScript.innerHTML));
      oldScript.parentNode.replaceChild(newScript, oldScript);
    });
  }

  async retrieveJson() {
    let schemaJson = this.props.schema;
    let uiSchemaJson = this.props.uiSchema;
    let formDataJson = this.props.formData;

    let url = this.attrs['form-dataset-url'];
    if (url) {
      const formDatasetReq = await axios.get(url);
      const formDataset = formDatasetReq.data;
      schemaJson = formDataset.schema;
      uiSchemaJson = formDataset.uiSchema;
      formDataJson = formDataset.formData;
    } else {
      url = this.attrs['schema-url'];
      if (url) {
        const schemaReq = await axios.get(url);
        schemaJson = schemaReq.data;
      }
      url = this.attrs['ui-schema-url'];
      if (url) {
        const uiSchemaReq = await axios.get(url);
        uiSchemaJson = uiSchemaReq.data;
      }
      url = this.attrs['form-schema-url'];
      if (url) {
        const formDataReq = await axios.get(url);
        formDataJson = formDataReq.data;
      }
    }

    return { schema: schemaJson, uiSchema: uiSchemaJson, formData: formDataJson };
  }

  mount() {
    const DownloadWidget = (props) => {
      let li = [];
      (props.value instanceof Array ? props.value : [props.value]).forEach(function (base64) {
        if (base64 == null) {
          li.push(
            <li className="list-group-item list-group-item-action">
              No file
            </li>
          );
        } else {
          let base64Parts = base64.split(';');
          let filename = decodeURI(base64Parts[1].split('=')[1]);

          li.push(
            <a className="list-group-item list-group-item-action" download={filename} href={props.value}>
              {filename}
            </a>
          );
        }
      })

      return (
        <ul className="list-group">
          {li}
        </ul>
      );
    };

    const widgets = {
      downloadWidget: DownloadWidget
    };

    const cssContent = `
  .nav-pills {
    margin-bottom: 20px;
  }
  
  .nav-pills > li {
      display: inline-block;
      margin-right: 10px;
  }
  
  .nav-pills > li > a {
      border-radius: 0.25rem;
      padding: 10px 15px;
      text-decoration: none;
  }
  
  .nav-pills > .active > a {
      background-color: #007bff;
  }
  
  .nav-pills > li > a {
      color: #007bff;
      border: 1px solid #007bff;
  }
  
  .nav-pills > li > a:hover {
      background-color: #0056b3;
  }
`;

    const StyleTag = React.createElement('style', {
      type: 'text/css'
    }, cssContent);

    this.retrieveJson().then((data) => {
      if (data.schema == null) return;

      // <link rel="stylesheet" href={this.attrs.cssHref || 'https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css'}></link>
      // <link rel="stylesheet" href={this.attrs.cssHref || 'https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css'}></link>

      let FormWithPagination = applyNavs(Form);
      this.root.render(
        <React.Fragment>
          <link rel="stylesheet" href={this.attrs.cssHref || 'https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css'}></link>

          {StyleTag}

          <FormWithPagination
            {...this.attrs}
            onSubmit={this.state.onSubmit}
            {...this.props}
            schema={data.schema}
            uiSchema={data.uiSchema}
            formData={data.formData}
            validator={validator}
            widgets={widgets}
          >
            {this.children.length > 0 && parse(this.innerHTML)}
          </FormWithPagination>

        </React.Fragment>
      );
    });
  }

  unmount() {
    this.root.unmount();
  }

  connectedCallback() {
    this.mount();
  }

  disconnectedCallback() {
    this.unmount();
  }

  static get observedAttributes() {
    return ['props-var'];
  }

  attributeChangedCallback(attrName, oldVal, newVal) {
    if (attrName == 'props-var') {
      this.props = {
        ...this.props,
        ...window[newVal]
      }
      this.mount();
    }
  }
}

customElements.define('react-form', ReactFormElement);
