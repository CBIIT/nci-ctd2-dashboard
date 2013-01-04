!function ($) {
    var onWhichSlide = 0;

	$(function(){
		$('#myCarousel').carousel('pause');

        var ctd2Boxes = $('.ctd2-boxes .span3');
        ctd2Boxes.hover( function() {
            $('.ctd2-boxes .span3').removeClass('active-box');
            var val = parseInt($(this).attr("data-order"));
            $(this).addClass('active-box');
            if(onWhichSlide != val) {
                $('#myCarousel').carousel(val);
            }
            onWhichSlide = val;
            $('#myCarousel').carousel('pause');
        });

        ctd2Boxes.first().addClass('active-box');

        $('#prevSlideControl').bind("click", function() {
            changeBoxFocus($('#myCarousel .active').index()-1);
        });

        $('#nextSlideControl').bind("click", function() {
            changeBoxFocus($('#myCarousel .active').index()+1);
        });


        var changeBoxFocus = function(idx) {
            // Do the modulo
            if(idx < 0) {
                idx += 4;
            } else {
                idx = idx % 4;
            }

            $('.ctd2-boxes .span3').removeClass('active-box');
            $('.ctd2-boxes .span3[data-order="' + idx + '"]').addClass('active-box');
        };

        $(".target-link").tooltip();
        $(".drug-link").tooltip();
        $(".genomics-link").tooltip();
        $(".story-link").tooltip();

        $("#target-search").typeahead({ source: targets, items: 3 });
        $("#drug-search").typeahead({ source: drugs, items: 3 });
        $("#alteration-search").typeahead({ source: targets, items: 3 });

    });
}(window.jQuery);
