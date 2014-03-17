if (!window.SADI) {
 window.SADI = {};
}
if (!window.SADI.init) {
 window.SADI.init = function($) {
  if (!window.SADI.bookmarkletClicked) {
   (function() {
    // support functions and data...
    var loadmask,
        do_loadmask = function(loading) {
            if (loading) {
                if (typeof loadmask === 'undefined') {
                    loadmask = $("<div id='PHL_loading'></div>")
                        .prependTo("body");
                } else {
                    loadmask.show();
                }
            } else {
                if (typeof loadmask !== 'undefined') {
                    loadmask.hide();
                }
            }
        },
        contents,
        match_template = $.template("PHL_match",
            "<a href='#\${a}' class='\${css}'>\${name}</a><br>" +
            "(matched as \"\${text}\")"),
        contents_template = $.template("PHL_contents",
          "<h1>Found \${n} drug mention{{if n != 1}}s{{/if}} in the page</h1>" +
          "<p class='instructions'>(click to toggle the list of mentions)</p>" +
            "<ol>{{each(i, match) matches}}" +
              "<li>{{tmpl(match) 'PHL_match'}}</li>" +
            "{{/each}}</ol>"),
        do_contents = function(drug_links) {
            if (typeof contents === 'undefined') {
                contents = $("<div id='PHL_contents'></div>")
                    .prependTo("body");
                contents.zmax();
                $.tmpl("PHL_contents", {
                          n: drug_links.length,
                    matches: $.map(drug_links, function(drug_link) {
                        var $drug_link = $(drug_link),
                            entity = $drug_link.data('entity'),
                            css = 'PHL_drug';
                            if ($drug_link.hasClass("PHL_dangerous")) {
                                css += " PHL_dangerous";
                            }
                        return {
                            text: $drug_link.text(),
                               a: drug_link.name,
                             css: css,
                            name: entity.label
                        };
                    })
                }).appendTo(contents);
                contents.find(".instructions").click(function(e) {
                    contents.find("ol").toggle();
                });
                contents.find("a").click(function(e) {
                    var aname = e.currentTarget.hash.substring(1),
                        css = "PHL_highlight";
                    $("." + css).removeClass(css);
                    $("a[name='" + aname + "']").addClass(css);
                });
            }
        },
        dialog,
        drug_template = $.template("PHL_drug",
            "<a class='PHL_drug' href='\${uri}'>\${name}</a>\${match}"),
        description_template = $.template("PHL_interaction_description",
            "because it causes <span class='PHL_description'>\${description}</span>"),
        references_template = $.template("PHL_references",
            "<span class='PHL_references'>" +
              "{{each(i, ref) references}}" +
                "{{if i > 0}}, {{/if}}" +
                "<a href='\${ref}'>[\${i + 1}]</a>" +
              "{{/each}}" +
            "</span>"),
        interaction_template = $.template("PHL_interaction",
            "<p>You are taking {{tmpl(prescribedDrug) 'PHL_drug'}}, which" +
            " should not be taken with {{tmpl(dangerousDrug) 'PHL_drug'}}" +
            " {{tmpl 'PHL_interaction_description'}}." +
            " {{tmpl 'PHL_references'}}</p>"),
        dialog_template = $.template("PHL_dialog",
            "<h1>Dangerous interaction!</h1>" +
              "{{each(i, interaction) interactions}}" + 
                "{{tmpl(interaction) 'PHL_interaction'}}" +
              "{{/each}}");
        do_dialog = function(e) {
            var i, entity = e.data;
            e.preventDefault();
            if (typeof dialog === 'undefined') {
                dialog = $("<div id='PHL_dialog'></div>")
                    .prependTo("body");
                dialog.click(function() {
                    dialog.hide();
                });
            }
            dialog.empty();
            $.tmpl("PHL_dialog", entity).appendTo(dialog);
            dialog.css({
                position: 'absolute',
                top: e.pageY + "px",
                left: e.pageX + "px"
            });
            dialog.show();
        };
    // main method...
    window.SADI.bookmarkletClicked = function() {
        do_loadmask(true);
        PHL.get_entities(function(entities) {
            var counter=1;
            entities.each(function(index, entity) {
//                console.log("found entity");
//                console.log(entity);
                $('body').PHL_highlight(
                    entity.match,
                    function(match) {
//                        console.log("found match");
//                        console.log(match);
                        var a = document.createElement('a');
                        a.className = 'PHL_drug';
                        a.href = entity.url;
                        a.title = entity.label;
                        a.name = "PHL_drug_" + counter++;
                        $(a).data('entity', entity);
                        return a;
                    });
            });
            PHL.find_interactions(entities, function(interactions) {
//                console.log("interactions");
//                console.log(interactions);
                var i, j, entity, interaction;
                for (i=0; i<interactions.length; ++i) {
                    interaction = interactions[i];
                    for (j=0; j<entities.length; ++j) {
                        entity = entities[j];
                        if (typeof(entity.interactions) === 'undefined') {
                            entity.interactions = [];
                        }
                        if ((entity.id === interaction.dangerousDrug.id) ||
                            (entity.id === interaction.prescribedDrug.id)) {
                            entity.interactions.push(interaction);
                        }
                    }
                }
                $('a.PHL_drug').each(function(index, element) {
                    var $this = $(this), entity = $this.data('entity');
                    if (entity && !$.isEmptyObject(entity.interactions)) {
                        $this.addClass('PHL_dangerous');
                        $this.click($this.data('entity'), do_dialog);
                    }
                });
                do_contents($('a.PHL_drug').get());
                do_loadmask(false);
            });
        });
    };
   })();
  }
  window.SADI.bookmarkletClicked();
 };
}

(function() {
    var jQueryScriptElement,
        jQueryScriptURL = 
//            "http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.js",
            "http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js",
        css = 
            "${deploy.root}/style/style.css",
        scripts = [
            "${deploy.js}/er.js",
            "${deploy.js}/highlight.js",
            "${deploy.js}/interact.js",
            "${deploy.js}/jquery.tmpl.js"
        ],
        initJQuery = function() {
            if (typeof jQuery === 'undefined') {
                if (!jQueryScriptElement) {
                    jQueryScriptElement = document.createElement('script');
                    jQueryScriptElement.setAttribute('src', jQueryScriptURL);
                    document.getElementsByTagName('body')[0].appendChild(jQueryScriptElement);
                }
                setTimeout(initJQuery, 50);
            } else {
                /* override jQuery.getScript to load multiple dependencies...
                 * (should work with all versions of jQuery > 1.0)
                 * TODO error handling
                 * (see http://api.jquery.com/jQuery.getScript/#handling-errors)
                 */
                (function($) {
                    var getScript = $.getScript,
                        getScriptDeferred = function(resources, callback) {
                            // based on http://jsfiddle.net/rwaldron/jgjHy/
                            var len = resources.length, 
                                deferreds = [],
                                i;
                            for (i=0 ; i<len; ++i) {
                                deferreds.push( getScript(resources[i]) );
                            }
                            $.when.apply(null, deferreds).then(callback);
                        },
                        getScriptNaive = function(resources, callback) {
                            var len = resources.length, 
                                count = 0,
                                handler = function() { if (++count >= len) { callback(); } },
                                i;
                            for (i=0; i<len; ++i) {
                                getScript(resources[i], handler);
                            }
                        };
                    if ($.fn.jquery >= "1.5") {
                        $.getScript = getScriptDeferred;
                    } else {
                        $.getScript = getScriptNaive;
                    }
                })(jQuery);
                /* add isEmptyObject for earlier jQuerys
                 */
                (function($) {
                    if (typeof $.isEmptyObject === 'undefined') {
                        $.isEmptyObject = function(obj) {
                            for (var name in obj) {
                                return false;
                            }
                            return true;
                        };
                    }
                })(jQuery);
                /* add isArray for versions of jQuery < 1.3
                 */
                (function($) {
                	if (typeof $.isArray === 'undefined') {
	                	$.isArray = function(object) {
	                		return Object.prototype.toString.call(object) === '[object Array]';
	                	};
                	}
                })(jQuery);
                /* add max z-index
                 */
                (function($) {
                    $.fn.zmax = function() {
                        var zmax = 0;
                        $("*").each(function() {
                            var cur = parseInt($(this).css('z-index'));
                            zmax = cur > zmax ? cur : zmax;
                        });
                        zmax += 1;
                        return this.each(function() {
                            $(this).css("z-index", zmax);
                        });
                    }
                })(jQuery);
                // load required scripts and style...
                (function ($) {
                    // load stylesheet...
                    if (!$("link[href='" + css + "']").length) {
                        $("<link href='" + css + "' rel='stylesheet'>").appendTo("head");
                    }
                    // load required scripts...
                    if (typeof($.rdf) === 'undefined') {
                        scripts.unshift("http://rdfquery.googlecode.com/files/jquery.rdfquery.core.min-1.0.js");
                    }
                    $.getScript(scripts, 
                        window.SADI.init.bind(this, $));
                })( jQuery );
            }
        };
    initJQuery();
})();
