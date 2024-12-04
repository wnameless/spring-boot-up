import css from '!!raw-loader!../css/bootswatch-3.4.1.css';
import React from 'react';

const Bs3StyleTag = () => {
  return (
    <style>{css}</style>
  );
};

export { Bs3StyleTag };
