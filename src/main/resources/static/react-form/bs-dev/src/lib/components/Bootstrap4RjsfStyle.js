import css from '!!raw-loader!../css/bootswatch-4.6.2.css';
import React from 'react';

const Bs4StyleTag = () => {
  return (
    <style>{css}</style>
  );
};

export { Bs4StyleTag };
