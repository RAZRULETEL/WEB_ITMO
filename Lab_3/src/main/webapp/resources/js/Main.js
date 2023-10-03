const MIN_X = -5, MAX_X = 3;
const MIN_Y = -3, MAX_Y = 5;
const MIN_R = 2, MAX_R = 5;
const AXES_OFFSET = 15;
window.onload = () => {
    const canvas = document.getElementsByTagName("canvas")[0];
    const ctx = canvas.getContext("2d");

    const observer = new MutationObserver((mutations, observer) => {
        for(let mutation of mutations) {
            if (mutation.target.classList.contains("table-offset-container")) {
                clearCanvas(ctx);
                drawAxes(ctx, AXES_OFFSET);
                redrawPoints();
                if(document.getElementById("parallel-table-button").classList.contains("parallel"))
                    updateParallels();
                // const trs = document.getElementById("dots-table").tBodies[0].rows;
                const trs = document.getElementById("dots-table").getElementsByTagName("table")[0].tBodies[0].rows;
                if(trs.length > 1)
                    document.getElementById("tooltip").innerText = document.getElementById("tooltip").innerText.replace("🕓", trs.item(trs.length - 1).classList.contains("success-text") ? "✅" : "❌" )
            }
        }
    });

    observer.observe(document, {
        subtree: true,
        childList:true
    });

    const Gradient = new RGBGradient();

    let params = new URLSearchParams(location.search);
    let madness_lvl = params.get('madness') == null ? 0 : params.get('madness');

    document.addEventListener("keyup", (e) => {
        if (e.code === "Space") madness_lvl = madness_lvl ? 0 : 1;
        if (e.code === "KeyR") {
            clearCanvas(ctx);
            drawAxes(ctx, AXES_OFFSET);
            redrawPoints();
        }
    });
    const input_y = document.getElementById("dot-controls:y");
    input_y.addEventListener("input", () => validateText(input_y, MIN_Y, MAX_Y));
    validateText(input_y, MIN_Y, MAX_Y);

    const input_r = document.getElementById("dot-controls:r");
    input_r.addEventListener("input", () => validateText(input_r, MIN_R, MAX_R));
    validateText(input_r, MIN_R, MAX_R);

    document.getElementById("canvas-turn-button").addEventListener("click", toggleCanvasTurn);
    document.getElementById("parallel-table-button").addEventListener("click", toggleParallelTable);

    init();

    function hideTooltip() {
        document.getElementById("tooltip").style.top = "-1000px";
        document.getElementById("tooltip").style.left = "-1000px";
    }

    function init() {
        drawAxes(ctx, AXES_OFFSET);
        redrawPoints();
    }

    let frame_counter = 0;

    let on_animation = () => {
        Gradient.paintChildrenTextRGB(document.getElementsByClassName("head")[0]);
        if (madness_lvl == 1) {
            clearCanvas(ctx);
            drawAxes(ctx, AXES_OFFSET, frame_counter / 100);
            redrawPoints();


        }
        window.requestAnimationFrame(on_animation);
        frame_counter++;
    };

    function toggleCanvasTurn(e) {
        hideTooltip();
        if (document.body.style.getPropertyValue("--swap-state") == "0") {
            if (e)
                e.target.children[0].style.transform = "rotate(360deg)";
            document.body.style.setProperty("--swap-state", "1");
        } else {
            if (e)
                e.target.children[0].style.transform = "rotate(0)";
            document.body.style.setProperty("--swap-state", "0");
        }
    }

    function toggleParallelTable(e) {
        hideTooltip();
        const table = document.getElementById("dots-table");
        let bg = "parallel";
        if (e.target.classList.contains("parallel")) {
            e.target.classList.remove("parallel");
            canvas.parentElement.style.removeProperty("margin-left");
            table.parentElement.parentElement.style.removeProperty("margin-left");
            table.parentElement.style.removeProperty("transform");
            table.parentElement.style.removeProperty("opacity");
            e.target.parentElement.style.removeProperty("right");
        } else {
            if (document.body.style.getPropertyValue("--swap-state") == "1")
                toggleCanvasTurn();
            e.target.classList.add("parallel");
            canvas.parentElement.style.marginLeft = "calc(25% - 250px + 20px)";
            table.parentElement.parentElement.style.marginLeft = "calc(75% - 250px - 20px)";
            table.parentElement.style.transform = "none";
            table.parentElement.style.opacity = "1";
            e.target.parentElement.style.right = "calc(50% - 25px)";
            bg = "converge";
        }
        setTimeout(() => {
            e.target.children[0].src = "/img/" + bg + ".png";
        }, 250);
    }

    function updateParallels(){
        const table = document.getElementById("dots-table");
        table.parentElement.parentElement.style.marginLeft = "calc(75% - 250px - 20px)";
        table.parentElement.style.transform = "none";
        table.parentElement.style.opacity = "1";
    }

    function redrawPoints() {
        // const table_body = document.getElementById("dots-table").tBodies[0];
        const table_body = document.getElementById("dots-table").getElementsByTagName("table")[0].tBodies[0];
        for (let i = 0; i < table_body.rows.length; i++) {
            if (table_body.rows[i].childElementCount >= 3) {
                const point = {
                    x: table_body.rows[i].children[0].innerText,
                    y: table_body.rows[i].children[1].innerText,
                    r: table_body.rows[i].children[2].innerText
                };
                drawPoint(ctx, point, table_body.rows[i].classList.contains("success-text") ? "#00c905" : "red", AXES_OFFSET);
            }
        }
    }

    window.requestAnimationFrame(on_animation);
};

function validateText(input, MIN, MAX) {
    if (input.value === "")
        input.setCustomValidity("Значение не введено!");
    else if (isNaN(input.value.replace(",", ".")))
        input.setCustomValidity("Значение должно быть числом!");
    else {
        const value = +input.value.replace(",", ".");
        if (value < MIN)
            input.setCustomValidity("Значение слишком маленькое, должно быть не меньше " + MIN);
        else if (value > MAX)
            input.setCustomValidity("Значение слишком большое, должно быть не больше " + MAX);
        else {
            input.setCustomValidity("");
            return true;
        }
    }
    return false;
}

function onCanvasClick(e){
    const tooltip = document.getElementById("tooltip");

    const canvas = document.getElementsByTagName("canvas")[0];

    if (!validateText(document.getElementById("dot-controls:r"), MIN_R, MAX_R)) {
        tooltip.innerText = "Радиус не указан!";
        tooltip.style.top = (e.pageY - tooltip.offsetHeight - 10) + "px";
        tooltip.style.left = (e.pageX - tooltip.offsetWidth / 2) + "px";
        return false;
    }
    let r = +document.getElementById("dot-controls:r").value;
    const graph_length = (canvas.width / 2 - AXES_OFFSET) * 2 / 3;
    const x = Math.floor((e.layerX - canvas.width / 2) / graph_length * r * 100) / 100;
    const y = Math.floor(-(e.layerY - canvas.height / 2) / graph_length * r * 100) / 100;

    tooltip.innerText = "x: " + x + ", y: " + y + ", r: " + r + "🕓";
    tooltip.style.top = (e.pageY - tooltip.offsetHeight - 10) + "px";
    tooltip.style.left = (e.pageX - tooltip.offsetWidth / 2) + "px";

    document.getElementById("hidden-canvas-form:x").value = x;
    document.getElementById("hidden-canvas-form:y").value = y;
    document.getElementById("hidden-canvas-form:r").value = r;

    return true;
}

function replaceColons(){
    const input_y = document.getElementById("dot-controls:y");
    input_y.value = input_y.value.replace(",", ".");

    const input_r = document.getElementById("dot-controls:r");
    input_r.value = input_r.value.replace(",", ".");
}