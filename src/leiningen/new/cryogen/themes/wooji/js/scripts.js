const rotate = (element, rotation, interval) => {
  /* Rotate an element: */
  let degrees = 0;
  setInterval(function () {
    degrees = (degrees === 360) ? 0 : degrees + rotation;
    $(element).css({"transform" : "rotate(" + degrees + "deg)"});
  }, interval);
};

const encode = function (s) {
  return s.replace(/[\u00A0-\u9999<>\&]/gim, function (i) {
    return `&#${i.charCodeAt(0)};`;
  });
};

$(document).ready(function () {
  /* Enable tooltip plugin: */
  $("[data-toggle=\"tooltip\"]").tooltip();

  /* Modify table with bootstrap tables: */
  $("table").addClass("table table-hover");

  /* Remove empty tags: */
  $("p:empty").remove();

  /* Remove empty h2s */
  $("h2:empty").remove();

  /* Table of Contents: */
  $(function () {
    let nav = ".toc";
    Toc.init($(nav));
    $("body").scrollspy({
      target: nav
    });

    /* Add anchor links to headings: */
    $(".toc a").each(function () {
      let text = $(this).text();
      let id = $(this).attr("href");
      let regex = /^[\w#]$/i; // match only alphanumeric and hash symbol
      let escapedId = id
          .split("")
          .map(s => (!regex.test(s)) ? `\\${s}` : s)
          .join("");

      /* Move .active to a clicked link: */
      $(this).click(function () {
        document.querySelector(escapedId).scrollIntoView({
          behavior: "smooth"
        });
      });
    });
  });

  /* Prepend anchor links to non-TOC headings: */
  $(".content-block")
    .find("h4,h5,h6")
    .each(function () {
      $(this).prepend(
        `<a class="post-anchor btn"
          href="#${$(this).attr("id")}">
           <i class="fas fa-link fa-xs"></i>
       </a>`
      );
    });

  /* Include Apple Siri wave: */
  let siriContainer = document.querySelector(".siri-container");
  let siriParentWidth = $(siriContainer).parent().width();
  let siriWave = new SiriWave({
    container: siriContainer,
    // style: "ios9",
    color: "#333333",
    // ratio: 0,
    speed: 0.01,
    amplitude: 1,
    frequency: 1,
    pixelDepth: 0.1,
    lerpSpeed: 0.1,
    width: siriParentWidth,
    height: 25,
    autostart: true,
  });

  /* Resize Apple Siri wave canvas: */
  let siriCanvas = $(siriContainer).find("canvas");
  $(siriCanvas).prop("width", siriParentWidth - 30);

  /* Rotate Yin Yang: */
  rotate(".pagination .separator i", 10, 100);

  /* Smooth transition (leaving page): */
  $(window).on("beforeunload", function () {
    $("body").show().fadeOut("slow");
  });


  /*
   * Change the previous and next texts
   * when orientation is portrait:
   */
  let PORTRAIT = "portrait-primary";
  let LANDSCAPE = "landscape-primary";
  let minWidth = 768;

  if (screen.orientation.type === PORTRAIT
      || $(window).width() <= minWidth) {
    let prevText = $(".pagination .previous")
        .text()
        .trim();
    let nextText = $(".pagination .next")
        .text()
        .trim();

    if (prevText) {
      $(".pagination .previous")
        .html("<i class=\"fas fa-angle-left\"></i> Newer")
        .attr({
          title: prevText,
          "data-toggle": "tooltip",
          "data-placement": "top"
        })
        .tooltip();
    }

    if (prevText) {
      $(".pagination .next")
        .html("Older <i class=\"fas fa-angle-right\"></i>")
        .attr({
          title: prevText,
          "data-toggle": "tooltip",
          "data-placement": "top"
        })
        .tooltip();
    }
  }
});
