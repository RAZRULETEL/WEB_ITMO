<%@ page import="java.util.ArrayList,types.PointCheckResult" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Place dot</title>

    <link rel="stylesheet" href="css/PointsGraphTable.css">
    <link rel="stylesheet" href="css/Main.css">
    <link rel="stylesheet" href="css/NiceSelect.css">
    <link rel="stylesheet" href="css/Header.css">
    <link rel="stylesheet" href="css/FormInputs.css">
</head>
<body onload="init()" style="--swap-state: 0;">
<div class="inputs-page
<% if(session.getAttribute("redirect") != null){
session.setAttribute("redirect", null);
%>
show
<% } %>
">
<div class="head">
    <pre>Лабораторная №2    Мартыненко Вадим Андреевич  P3232    Вариант 99899</pre>
</div>
<div class="points-info">

    <canvas width="500" height="500"></canvas>

    <div class="table-offset-container">
        <div class="table-container">
            <table id="dots-table">
                <thead>
                <tr>
                    <th>x</th>
                    <th>y</th>
                    <th>r</th>
                    <th style="padding: 0;">
                        <label class="custom-select">
                            <select id="table-time-format" onchange="updateTimeColumn()">
                                <option value="0">timestamp, ms</option>
                                <option value="1">Дата и время</option>
                                <option value="2">Дата и время (кратко)</option>
                                <option value="3">Точное время</option>
                            </select>
                            <svg>
                                <use xlink:href="#select-arrow-down"></use>
                            </svg>
                        </label></th>
                    <th>Время выполнения, ms</th>
                </tr>
                </thead>
                <tbody>
                <%
                    ArrayList <PointCheckResult> points = (ArrayList<PointCheckResult>) session.getAttribute("points");
                    if(points != null)
                        for(PointCheckResult point : points){
                %>
                    <tr style="color:
                    <%= point.getSuccess() ? "#00c905" : "red" %>
                    ;">
                        <td><%= point.getX() %></td>
                        <td><%= point.getY() %></td>
                        <td><%= point.getR() %></td>
                        <td><%= point.getTimestamp() %></td>
                        <td><%= point.getExecutionTime() %></td>
                    </tr>
                        <%
                        }
                        %>
                        <tr id="points-table-filler">
                            <td style="height: auto;" colspan="5"></td>
                        </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div class="table-controls-container">
        <div class="table-controls">
            <button id="canvas-turn-button"><img src="/img/refresh.png" alt=""></button>
            <button id="parallel-table-button"><img src="/img/parallel.png" alt=""></button>
        </div>
    </div>
</div>
<form id="dot-controls" method="GET" onsubmit="return delayedSubmit();">
    <div>
        <label class="custom-select">
            <select name="x" id="x">
                <option value="" disabled selected>x</option>
            </select>
            <svg>
                <use xlink:href="#select-arrow-down"></use>
            </svg>
        </label>
        <input type="text" id="y" name="y" autocomplete="off" placeholder="y">
        <table id="radius-selector">
            <tr>
                <td><label for="r-1">1
                    <input type="checkbox" id="r-1" value="1" name="r"></label></td>
                <td><label for="r-2">1.5
                    <input type="checkbox" id="r-2" value="1.5" name="r"></label></td>
                <td><label for="r-3">2
                    <input type="checkbox" id="r-3" value="2" name="r"></label></td>
            </tr>
            <tr>
                <td></td>
                <td><label for="r-5">3
                    <input type="checkbox" id="r-5" value="3" name="r"></label></td>
                <td><label for="r-4">2.5
                    <input type="checkbox" id="r-4" value="2.5" name="r"></label></td>
            </tr>
        </table>
    </div>
    <div>
        <input type="button" id="clear" onclick="onClearButtonClick()" value="Очистить">
        <input type="submit">
    </div>
</form>
</div>

<div id="tooltip" style="top: -1000px;left: -1000px">
    <span class="tooltip-text">Подсказка</span>
</div>
<!-- SVG Sprites-->
<svg class="sprites">
    <symbol id="select-arrow-down" viewBox="0 0 10 6">
        <polyline points="1 1 5 5 9 1"></polyline>
    </symbol>
</svg>
<script src="js/GraphTools.js"></script>
<script>
    const MIN_X = -2, STEP_X = 0.5, MAX_X = 2;
    const MIN_Y = -5, MAX_Y = 5;
    const AXES_OFFSET = 15;

    let params = new URLSearchParams(location.search);
    let madness_lvl = params.get('madness') == null ? 0 : params.get('madness');

    const table_body = document.getElementById("dots-table").tBodies[0];

    document.addEventListener("keyup", (e) => {
        if (e.code === "Space") madness_lvl = madness_lvl ? 0 : 1;
        if (e.code === "AltRight") {
            let checkboxes = document.getElementsByName("r");
            for (let check of checkboxes)
                check.setAttribute("type", "radio");
        }
        if (e.code === "KeyR"){
            clearCanvas(ctx);
            drawAxes(ctx, AXES_OFFSET);
            redrawPoints();
        }
    });

    const value_x = document.getElementById("x");
    value_x.addEventListener("input", () => validateX());
    validateX();

    const input_y = document.getElementById("y");
    input_y.addEventListener("input", () => validateY());
    validateY();

    const checkboxes = document.getElementsByName("r");
    checkboxes.forEach(checkbox =>
        checkbox.addEventListener("change", () => {
            for (let check of checkboxes) {
                if (check !== checkbox)
                    check.checked = false;
            }
            validateR();
        }));
    validateR();

    document.getElementById("canvas-turn-button").addEventListener("click", toggleCanvasTurn);
    document.getElementById("parallel-table-button").addEventListener("click", toggleParallelTable);

    const canvas = document.getElementsByTagName("canvas")[0];
    const ctx = canvas.getContext("2d");
    canvas.addEventListener("click", e => {
        const tooltip = document.getElementById("tooltip");

        if (!validateR()) {
            tooltip.innerText = "Радиус не указан!";
            tooltip.style.top = (e.pageY - tooltip.offsetHeight - 10) + "px";
            tooltip.style.left = (e.pageX - tooltip.offsetWidth / 2) + "px";
            return;
        }
        let r = 0;
        for (let check of checkboxes)
            if (check.checked)
                r = check.value;
        const graph_length = (canvas.width / 2 - AXES_OFFSET) * 2 / 3;
        const x = Math.floor((e.layerX - canvas.width / 2) / graph_length * r * 100) / 100;
        const y = Math.floor(-(e.layerY - canvas.height / 2) / graph_length * r * 100) / 100;

        tooltip.innerText = "x: " + x + ", y: " + y + ", r: " + r + "🕓";
        tooltip.style.top = (e.pageY - tooltip.offsetHeight - 10) + "px";
        tooltip.style.left = (e.pageX - tooltip.offsetWidth / 2) + "px";


        const xhttp = new XMLHttpRequest();
        xhttp.onload = function () {
            console.log(this);
            if (this.readyState === 4 && this.status === 200) {
                try {
                    const point = JSON.parse(this.response);
                    if(point.error == null) {
                        addPointToTable(point);
                        drawPoint(ctx, point, "rgb(" + rand(100, 255) + "," + rand(100, 255) + ", " + rand(100, 255) + ")", AXES_OFFSET);
                        tooltip.innerText = tooltip.innerText.replace("🕓", point.success ? "✅" : "❌");
                    } else {
                        alert(point.error);
                        hideTooltip();
                    }
                } catch (e) {
                    alert(this.response);
                    hideTooltip();
                }
            }
        }

        xhttp.open("GET", "/?" + "x=" + x + "&y=" + y + "&r=" + r)
        xhttp.setRequestHeader("Accept", "application/json")
        xhttp.send();
    });

    function hideTooltip(){
        document.getElementById("tooltip").style.top = "-1000px";
        document.getElementById("tooltip").style.left = "-1000px";
    }

    const RGBGradient = {
        speed: 2,
        last_speed: 0,
        degree: 30,
        colors: [
            [255, 0, 0],
            [255, 125, 0],
            [255, 255, 0],
            [125, 255, 0],
            [0, 255, 0],
            [0, 255, 125],
            [0, 255, 255],
            [0, 125, 255],
            [0, 0, 255],
            [125, 0, 255],
            [255, 0, 255],
            [255, 0, 125],
        ],
        next: function () {
            const speed = this.speed;
            for (let i = 0; i < this.colors.length; i++) {
                let r = this.colors[i][0] + this.last_speed - speed;
                let g = this.colors[i][1] + this.last_speed - speed;
                let b = this.colors[i][2] + this.last_speed - speed;
                if (r >= 254 - speed) {
                    if (b >= 1 + speed)
                        b -= speed;
                    else if (g <= 255 - speed)
                        g += speed;
                }
                if (g >= 255 - speed) {
                    if (r >= 1 + speed)
                        r -= speed;
                    else if (b <= 255 - speed)
                        b += speed;
                }
                if (b >= 255 - speed) {
                    if (g >= 1 + speed)
                        g -= speed;
                    else if (r <= 255 - speed)
                        r += speed;
                }
                this.colors[i][0] = Math.floor(r * 10) / 10;
                this.colors[i][1] = Math.floor(g * 10) / 10;
                this.colors[i][2] = Math.floor(b * 10) / 10
            }
            this.last_speed = speed;
        },
        buildGradientString: function (offsetColors = 0, reverseOrder = true) {
            let colors = this.colors;
            if (offsetColors !== 0)
                colors = [...colors.slice(offsetColors), ...colors.slice(0, offsetColors)];
            let gradient = "-webkit-linear-gradient(" + this.degree + "deg,";
            if (reverseOrder) {
                for (let i = colors.length - 1; i > 0; i--) {
                    if (i !== colors.length - 1)
                        gradient += ","
                    gradient += "rgb(" + colors[i].toString() + ")"
                }
                gradient += ",rgb(" + colors[colors.length - 1].toString() + "))";
            } else {
                for (let i = 0; i < colors.length; i++) {
                    if (i !== 0)
                        gradient += ","
                    gradient += "rgb(" + colors[i].toString() + ")"
                }
                gradient += ",rgb(" + colors[0].toString() + "))";
            }
            return gradient;
        }
    };

    function init() {
        generateXOptions();
        drawAxes(ctx, AXES_OFFSET);
    }

    let frame_counter = 0;

    let on_animation = () => {
        paintChildrenTextRGB(document.getElementsByClassName("head")[0]);
        if (madness_lvl == 1) {
            if (frame_counter % 20 === 0)
                checkboxSnake();
            if (frame_counter % 10 === 0)
                selectMess(value_x);
            clearCanvas(ctx);
            drawAxes(ctx, AXES_OFFSET, frame_counter / 100);
            redrawPoints();


        }
        window.requestAnimationFrame(on_animation);
        frame_counter++;
    };

    function paintChildrenTextRGB(parent, angle = 45) {
        const children = parent.children;
        RGBGradient.angle = angle;
        // document.body.style.background = RGBGradient.buildGradientString(0);
        for (let i = 0; i < children.length; i++) {
            children[i].style.background = RGBGradient.buildGradientString(i);
            children[i].style["-webkit-background-clip"] = "text";
            children[i].style["-webkit-text-fill-color"] = "transparent";
        }
        RGBGradient.next();
    }

    function validateX(){
        if (value_x.selectedOptions[0].value === "")
            value_x.setCustomValidity("Выберите значение !");
        else
            value_x.setCustomValidity("");
    }

    function validateY() {
        if (input_y.value === "")
            input_y.setCustomValidity("Значение не введено!");
        else if (isNaN(input_y.value.replace(",", ".")))
            input_y.setCustomValidity("Значение должно быть числом!");
        else {
            const value = +input_y.value;
            console.log(value);
            if (value < MIN_Y)
                input_y.setCustomValidity("Значение слишком маленькое, должно быть не меньше " + MIN_Y);
            else if (value > MAX_Y)
                input_y.setCustomValidity("Значение слишком большое, должно быть не больше " + MAX_Y);
            else {
                input_y.setCustomValidity("");
                return true;
            }
        }
        return false;
    }

    function validateR() {
        for (let check of checkboxes) {
            if (check.checked) {
                checkboxes.forEach(check => check.setCustomValidity(""));
                return true;
            }
        }
        checkboxes[(checkboxes.length - checkboxes.length % 2) / 2].setCustomValidity("Выберите значение!");
        return false;
    }

    function onSendButtonClick() {
        const form = document.getElementById("dot-controls");
        if (!form.reportValidity())
            return;
        const xhttp = new XMLHttpRequest();
        xhttp.onload = function () {
            console.log(this);
            if (this.readyState === 4 && this.status === 200) {
                try {
                    const point = JSON.parse(this.response);
                    // addPointToTable(point);
                    drawPoint(ctx, point, point.success ? "green" : "red");
                } catch (e) {
                    alert(this.response);
                }
            } else {
                alert(this.response);
            }
        }

        const formData = new FormData(form);

        let data = [];

        for (const [key, value] of formData)
            data.push(`${key}=${value.replace(".", ",")}`);

        xhttp.open("GET", "point.php?" + data.join("&"));
        xhttp.send();
    }


    function loadPointsData() {// Deprecated
        const xhttp = new XMLHttpRequest();
        xhttp.onload = function () {
            console.log(this.response);
            if (this.status === 200) {
                const data = JSON.parse(this.response);//JSON.parse("[{\"x\":1, \"y\":2, \"r\":4, \"success\":false, \"timestamp\":1691567031.1242, \"exec_time\":0.03048},{\"x\":1, \"y\":2, \"r\":3, \"success\":false, \"timestamp\":1691573306.7721, \"exec_time\":0.030007},{\"x\":2, \"y\":0, \"r\":4, \"success\":false, \"timestamp\":1691573318.4273, \"exec_time\":0.032112},{\"x\":2, \"y\":0, \"r\":4, \"success\":false, \"timestamp\":1691573320.5566, \"exec_time\":0.031426}]");
                for (const point of data) {
                    // addPointToTable(point);
                    drawPoint(ctx, point, point.success ? "green" : "red");
                }
            }
        }

        xhttp.open("GET", "point.php?list");
        xhttp.send();
    }


    function checkboxSnake() {
        const trs = document.getElementById("radius-selector").tBodies[0].rows;
        const tds = [...trs[1].cells];
        for (let i = trs[0].cells.length - 1; i >= 0; i--)
            tds.push(trs[0].cells[i]);
        let last_td = tds[tds.length - 1],
            last_child = last_td.children[0];

        for (let i = 1; i < tds.length; i++) {
            let buff = tds[i - 1];
            let buff_child = buff.children[0];
            if (tds[i - 1].children[0] !== undefined)
                tds[i - 1].removeChild(tds[i - 1].children[0]);
            if (last_child !== undefined) {
                if (rand(0, 1)) last_child.children[0].setAttribute("type", last_child.children[0].getAttribute("type") === "radio" ? "checkbox" : "radio");
                tds[i - 1].appendChild(last_child);
            }
            last_td = buff;
            last_child = buff_child;
        }
        if (last_child !== undefined)
            tds[tds.length - 1].appendChild(last_child);
    }

    function selectMess(select) {
        const options = Array.of(...select.options).filter(e => e.value !== "");
        let values = [];
        for (let i = 0; i < options.length; i++)
            values.push(options[i].value);
        values = values.sort(() => rand(-1, 1)).sort(() => rand(-1, 1)).sort(() => rand(-1, 1));
        for (let i = 0; i < options.length; i++) {
            options[i].value = values[i];
            options[i].innerText = values[i];
        }
    }

    function rand(min, max) {
        min = Math.ceil(min);
        max = Math.floor(max);
        return Math.floor(Math.random() * (max - min + 1) + min); // The maximum is inclusive and the minimum is inclusive
    }

    function generateXOptions() {
        let selectX = document.getElementById("x");
        for (let i = MIN_X; i <= MAX_X; i += STEP_X) {
            let option = document.createElement("option");
            option.value = i;
            option.innerText = i;
            selectX.append(option);
        }
    }

    function onClearButtonClick() {
        clearCanvas(ctx);
        drawAxes(ctx);
        sessionStorage.clear();
    }

    function delayedSubmit() {
        document.getElementsByClassName("inputs-page")[0].classList.add("hide");
        setTimeout(function () {
            document.getElementById("dot-controls").submit();
        }, 1000);
        return false;
    }

    function updateTimeColumn() {
        const format = +document.getElementById("table-time-format").value;
        let time_cells = [];
        for (const tr of table_body.children)
            for (const td of tr.children)
                if (td.getAttribute("timestamp"))
                    time_cells.push(td);
        switch (format) {
            case 0:
                time_cells.forEach(e => e.innerText = e.getAttribute("timestamp"));
                break;
            case 1:
                time_cells.forEach(e => e.innerText = new Date(+e.getAttribute("timestamp")));
                break;
            case 2:
                time_cells.forEach(e => {
                    const date = new Date(+e.getAttribute("timestamp"));
                    let date_string = (date.getDay() < 10 ? "0" : "") + date.getDay() + "." + (date.getMonth() < 10 ? "0" : "") + (date.getMonth() + 1) + "." + date.getFullYear();
                    date_string += " " + date.toLocaleTimeString();
                    e.innerText = date_string;
                });
                break;
            case 3:
                time_cells.forEach(e => {
                    const date = new Date(+e.getAttribute("timestamp"));
                    e.innerText = date.toLocaleTimeString() + "." + date.getMilliseconds();
                });
                break;
        }
    }

    function addPointToTable(point) {
        const tr = document.createElement("tr");
        tr.style.color = point.success ? "#00c905" : "red";

        const x = document.createElement("td");
        x.innerText = point.x;
        const y = document.createElement("td");
        y.innerText = point.y;
        const r = document.createElement("td");
        r.innerText = point.r;
        const timestamp = document.createElement("td");
        timestamp.setAttribute("timestamp", point.timestamp);
        timestamp.innerText = point.timestamp;
        const execution_time = document.createElement("td");
        execution_time.innerText = point.exec_time;


        tr.append(x, y, r, timestamp, execution_time);
        document.getElementById("points-table-filler").before(tr);
    }

    function toggleCanvasTurn(e) {
        hideTooltip();
        if (document.body.style.getPropertyValue("--swap-state") == "0") {
            if(e)
                e.target.children[0].style.transform = "rotate(360deg)";
            document.body.style.setProperty("--swap-state", "1");
        }else{
            if(e)
                e.target.children[0].style.transform = "rotate(0)";
            document.body.style.setProperty("--swap-state", "0");
        }
    }

    function toggleParallelTable(e){
        hideTooltip();
        const table = document.getElementById("dots-table");
        let bg = "parallel";
        if(e.target.classList.contains("parallel")){
            e.target.classList.remove("parallel");
            canvas.style.removeProperty("margin-left");
            table.parentElement.parentElement.style.removeProperty("margin-left");
            table.parentElement.style.removeProperty("transform");
            table.parentElement.style.removeProperty("opacity");
            e.target.parentElement.style.removeProperty("right");
        }else{
            if(document.body.style.getPropertyValue("--swap-state") == "1")
                toggleCanvasTurn();
            e.target.classList.add("parallel");
            canvas.style.marginLeft = "calc(25% - 250px + 20px)";
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

    function redrawPoints(){
        for (let i = 0; i < table_body.rows.length - 1; i++) {
            if(table_body.rows[i].childElementCount >= 3) {
                const point = {
                    x: table_body.rows[i].children[0].innerText,
                    y: table_body.rows[i].children[1].innerText,
                    r: table_body.rows[i].children[2].innerText
                };
                drawPoint(ctx, point, table_body.rows[i].style.color, AXES_OFFSET);
            }
        }
    }

    window.requestAnimationFrame(on_animation);
</script>
</body>
</html>