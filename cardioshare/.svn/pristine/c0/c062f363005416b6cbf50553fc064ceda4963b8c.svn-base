/* usage: $('#foo').highlight( probe, function(match) );
 */
;(function( $ ) {

	$.fn.PHL_highlight = function(probe, f) {
        // convert probe to regex, if necessary...
        if (!(probe instanceof RegExp)) {
            probe = probe.replace(/(\W)/g, "$1?");
            probe = probe.replace(/\s+/g, "\\s+");
            probe = new RegExp(probe, "ig");
        }
        // create default function, if necessary...
        if (typeof(f) !== 'function') {
            f = function(match) {
                var span = document.createElement('span');
                span.className = 'highlight';
                return span;
            }
        }
        // do the match...
        return this.each(function(index, element) {
            highlight_node(element, probe, f);
        });
    };

    /* based on http://johannburkard.de/resources/Johann/jquery.highlight-3.js
     * and https://github.com/jbr/jQuery.highlightRegex
     */
    var highlight_node = function(node, regex, f) {
        var pos, match, matchNode, matchClone, parentNode, replacement;
        normalize(node);
        if (node.nodeType === 3) {
            while (node.data && (pos = node.data.search(regex)) >= 0 ) {
                match = node.data.slice(pos).match(regex)[0];
                if (match.length > 0) {
                    replacement = f(match);
                    if (typeof(replacement) === 'string') {
                        replacement = document.createTextNode(replacement);
                    }
                    parentNode = node.parentNode;
                    matchNode = node.splitText(pos);
                    // note that this moves the search along...
                    node = matchNode.splitText(match.length);
                    matchClone = matchNode.cloneNode(true);
                    replacement.appendChild(matchClone);
                    parentNode.replaceChild(replacement, matchNode);
                } else {
                    // why would this happen?
                    break;
                }
            }
        } else if (node.nodeType === 1 && node.childNodes && !/(script|style)/i.test(node.tagName)) {
            $.each($.makeArray(node.childNodes), function(index, element) {
                highlight_node(element, regex, f);
            });
        }
    };
    var normalize = function(node) {
        if (!(node && node.childNodes))
            return;
        var children = $.makeArray(node.childNodes),
            prevTextNode = null;
        $.each(children, function(i, child) {
            if (child.nodeType === 3) {
                if (child.nodeValue === "") {
                    node.removeChild(child);
                } else if (prevTextNode !== null) {
                    prevTextNode.nodeValue += child.nodeValue;
                    node.removeChild(child);
                } else {
                    prevTextNode = child;
                }
            } else {
                prevTextNode = null
                normalize(child);
            }
        });
    };
})( jQuery );
