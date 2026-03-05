import createCache from '@emotion/cache';
import { CacheProvider } from '@emotion/react';
import React, { useMemo } from 'react';
import Select from 'react-select';

// Keep this import
void React;

const FileListWidget = (props) => {
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
        <li className="list-group-item list-group-item-action">
          {filename}
        </li>
      );
    }
  })

  return (
    <ul className="list-group">
      {li}
    </ul>
  );
};

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

const ImageWidget = (props) => {
  let li = [];
  (props.value instanceof Array ? props.value : [props.value]).forEach(function (base64) {
    if (base64 == null) {
      li.push(
        <li className="list-group-item list-group-item-action">
          No Image
        </li>
      );
    } else {
      let base64Parts = base64.split(';');
      let dataType = base64Parts[0];
      let filename = decodeURI(base64Parts[1].split('=')[1]);

      li.push(
        <a href="#" class="list-group-item list-group-item-action">
          <h5 class="mb-1">{filename}</h5>
          <img src={base64} class="img-fluid"></img>
        </a>
      );
    }
  })

  return (
    <ul className="list-group">
      {li}
    </ul>
  );
}

const LinkWidget = (props) => {
  const { value, onChange, readonly, schema } = props;

  if (readonly) {
    return (
      <a href={value} target="_blank" rel="noopener noreferrer">
        {schema.title || value}
      </a>
    );
  }

  return (
    <input
      type="url"
      value={value || ''}
      onChange={(e) => onChange(e.target.value)}
    />
  );
};

const SearchableSelectWidget = (props) => {
  const { schema, value, onChange, disabled, readonly, formContext } = props;

  const emotionCache = useMemo(() => {
    const container = formContext?.shadowRoot
      ? (() => {
          let nonce = document.createElement('style');
          formContext.shadowRoot.insertBefore(nonce, formContext.shadowRoot.firstChild);
          return nonce;
        })()
      : undefined;
    return createCache({ key: 'rsel', container });
  }, [formContext?.shadowRoot]);

  const options = (schema.enum || []).map((val, i) => ({
    value: val,
    label: schema.enumNames?.[i] ?? String(val),
  }));

  return (
    <CacheProvider value={emotionCache}>
      <Select
        options={options}
        value={options.find(opt => opt.value === value) ?? null}
        onChange={(selected) => onChange(selected ? selected.value : undefined)}
        isDisabled={disabled || readonly}
        isClearable
      />
    </CacheProvider>
  );
}

export { DownloadWidget, FileListWidget, ImageWidget, LinkWidget, SearchableSelectWidget };

