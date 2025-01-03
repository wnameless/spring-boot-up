/**
 * Safely set innerHTML and re-run <script> blocks.
 *
 * @param {Element} elm - The DOM element to receive the HTML content
 * @param {string} html - The raw HTML string to insert
 */
export function setInnerHTML(elm, html) {
  if (!elm || typeof html !== 'string') {
    console.warn('setInnerHTML: invalid arguments', { elm, html });
    return;
  }

  // 1) Optional: If you'd like to sanitize the HTML, call a sanitizer here.
  // e.g. DOMPurify.sanitize(html) - but that requires a library.

  // 2) Assign innerHTML
  elm.innerHTML = html;

  // 3) Re-run <script> tags so they execute
  //    If we don't do this, scripts inserted via innerHTML won't run.
  const scripts = elm.querySelectorAll('script');

  scripts.forEach((oldScript) => {
    // Create new script
    const newScript = document.createElement('script');

    // Copy attributes
    Array.from(oldScript.attributes).forEach((attr) => {
      newScript.setAttribute(attr.name, attr.value);
    });

    // If there's inline script text, copy it
    // NOTE: for external scripts (src="..."), the browser will fetch anew
    if (oldScript.innerHTML.trim()) {
      newScript.appendChild(document.createTextNode(oldScript.innerHTML));
    }

    // Replace old with new
    oldScript.parentNode?.replaceChild(newScript, oldScript);
  });
}