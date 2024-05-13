import React from 'react';

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

const NavStyleTag = React.createElement('style', {
  type: 'text/css'
}, cssContent);

export { NavStyleTag };
