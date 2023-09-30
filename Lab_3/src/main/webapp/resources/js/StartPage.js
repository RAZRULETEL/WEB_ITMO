const onLoad = () => {
    const Gradient = new RGBGradient();

    const variant = 8272722;
    const variantElement = document.getElementById("variant");

    const watches = document.getElementById("watches");
    setInterval(() => {
        watches.innerText = getFormattedDateTime();
    }, 10_000);
    watches.innerText = getFormattedDateTime();

    const form = document.getElementById("mapper_form");
    const avoidingButton = new CursorAvoidButton(document.getElementById("mapper_form:remap"), true, 250);
    avoidingButton.HTMLElement.style.transition = "0.1s";
    avoidingButton.borderColor = "#12da00";

    let madness = 1;

    Gradient.paintChildrenTextRGB(document.getElementsByClassName("head")[0]);

    let frameCounter = 0;

    window.addEventListener("keyup", (e) => {
        if(e.code === "Space"){
            madness = !madness;
            if(!madness)
                variantElement.innerText = variant;
            avoidingButton.setAvoiding = !avoidingButton.isAvoiding;
        }
    });

    const onResize = () => {
        form.style.height = calculateFormSize(form);
    };

    window.addEventListener("resize", onResize);
    const resizeObserver = new ResizeObserver((entries) => {
        onResize();
    });

    [document.body, ...document.body.children].forEach(e => resizeObserver.observe(e));
    onResize();

    let on_animation = () => {
        Gradient.paintChildrenTextRGB(document.getElementsByClassName("head")[0]);

        if(madness) {
            variantElement.innerText = variantElement.innerText.split("").map(e => rand(0, 9)).join("");

            if (frameCounter++ % 5 === 0)
                variantElement.innerText += rand(0, 9);
        }

        window.requestAnimationFrame(on_animation);
    };

    window.requestAnimationFrame(on_animation);
}


if(document.readyState === "complete")
    onLoad();
else
    window.onload = onLoad;

function rand(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1) + min); // The maximum is inclusive and the minimum is inclusive
}

function getFormattedDateTime() {
    const date = new Date();
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear().toString();
    const time = date.toLocaleTimeString();

    return `${day}.${month}.${year} ${time}`;
}

function calculateFormSize(form){
    return (document.body.clientHeight
        - [...document.body.children].reduce((sum, e) => sum + e.clientHeight, 0)
        + form.clientHeight) + "px";
}