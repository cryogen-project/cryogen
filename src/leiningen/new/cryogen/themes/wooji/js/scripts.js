const rotate = (element, rotation, interval) => {
  /* Rotate an element: */
  let degrees = 0;
  setInterval(function () {
    degrees = (degrees === 360) ? 0 : degrees + rotation;
    $(element).css({"transform" : "rotate(" + degrees + "deg)"});
  }, interval);
};

$(document).ready(function () {
  /* Smooth transition (onload page): */
  $("body").hide().fadeIn("slow");

  /* Enable tooltip plugin: */
  $("[data-toggle=\"tooltip\"]").tooltip();

  /* Modify table with bootstrap tables: */
  $("table").addClass("table table-hover");

  /* Remove empty tags: */
  $("p:empty").remove();

  /* Convert empty h2 with hr: */
  $("h2:empty").replaceWith("<hr />");

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

      $(escapedId).each(function () {
        $(this).prepend(
          `<a class="post-anchor btn-light"
              href="${id}">
               <i class="fas fa-link"></i>
           </a>`
        );
      });

      /* Move .active to a clicked link: */
      $(this).click(function () {
        $(".toc a").removeClass("active");
        document.querySelector(escapedId).scrollIntoView({
          behavior: "smooth"
        });
      });
    });
  });

  /* Highlight TOC on scroll: */
  $(function () {
    $(window).scroll(function () {
      $(":header").each(function () {
        if ($(window).scrollTop() >= $(this).offset().top) {
          let id = $(this).attr("id");
          $(".toc a").removeClass("active");
          $(`.toc a[href="#${id}"]`).addClass("active");
        }
      });
    });
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
    height: 50,
    autostart: true,
  });

  /* Resize Apple Siri wave canvas: */
  let siriCanvas = $(siriContainer).find("canvas");
  $(siriCanvas).prop("width", siriParentWidth - 30);

  /* Rotate Yin Yang: */
  rotate(".pagination .separator", 10, 100);

  /* Smooth transition (leaving page): */
  $(window).on("beforeunload", function () {
    $("body").show().fadeOut("slow");
  });
});
