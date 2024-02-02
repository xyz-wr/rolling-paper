document.addEventListener("DOMContentLoaded", function () {
    let contentNavItems = document.querySelectorAll('.content-nav ul li');

    contentNavItems.forEach(function (item) {
        item.addEventListener('click', function () {
            // 모든 li에서 active 클래스 제거
            contentNavItems.forEach(function (li) {
                li.classList.remove('active');
            });

            // 현재 클릭한 li에 active 클래스 추가
            item.classList.add('active');
        });
    });

    let footerNavItems = document.querySelectorAll('.footer-nav ul li');

    footerNavItems.forEach(function (item) {
        item.addEventListener('click', function () {
            // 모든 li에서 active 클래스 제거
            footerNavItems.forEach(function (li) {
                li.classList.remove('active');
            });

            // 현재 클릭한 li에 active 클래스 추가
            item.classList.add('active');
        });
    });

});
