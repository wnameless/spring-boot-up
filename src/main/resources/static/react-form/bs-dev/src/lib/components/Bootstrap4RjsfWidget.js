import React from 'react';

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

export { DownloadWidget, FileListWidget, ImageWidget, LinkWidget };

