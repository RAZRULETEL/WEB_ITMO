class CursorAvoidButton {
    #isRunning = false;
    maxBorderWidth = 7;
    angle3d = 35;
    borderStyle = "solid";
    borderColor = "#000";
    #isAvoiding;
    #cx;    #cy; // Center coordinates, without translate
    #HTMLElement;

    #onMouseMove = (event) => {
        if(this.#cx && this.#cy) {
            const dist = this.#getDistanceVectorToCursor(event);
            if (dist.length < this.radius) {
                const vec_x = -(dist.x / dist.length) * (this.radius - dist.length);
                const vec_y = -(dist.y / dist.length) * (this.radius - dist.length);

                this.#HTMLElement.style.translate = `${vec_x}px ${vec_y}px`;

                const angle_x = -Math.sin(vec_x / this.radius * Math.PI);
                const angle_y = Math.sin(vec_y / this.radius * Math.PI);
                const angle_deg = (1 - dist.length / this.radius) * this.angle3d;
                this.#HTMLElement.style.transform = `rotate3d(${angle_y}, ${angle_x}, 0, ${angle_deg}deg)`;

                this.#HTMLElement.style["border-" + (vec_y < 0 ? "top" : "bottom")] = Math.abs(Math.sin(vec_y / this.radius * Math.PI / 2)) * this.maxBorderWidth + "px " + this.borderStyle + " " + this.borderColor;
                this.#HTMLElement.style["border-" + (vec_y < 0 ? "bottom" : "top")] = "none";

                this.#HTMLElement.style["border-" + (vec_x < 0 ? "left" : "right")] = Math.abs(Math.sin(vec_x / this.radius * Math.PI / 2)) * this.maxBorderWidth + "px " + this.borderStyle + " " + this.borderColor;
                this.#HTMLElement.style["border-" + (vec_x < 0 ? "right" : "left")] = "none";

                this.#HTMLElement.style.boxShadow = `${Math.abs(angle_x) * 5}px ${Math.abs(vec_y) / this.radius * 70}px 12px ${(Math.abs(vec_y) / this.radius) * 12 - 4}px rgba(0, 0, 0, ${(1 - (Math.abs(vec_y) / this.radius)) / 2})`;
            } else {
                this.#isRunning = false;
                document.removeEventListener("mousemove", this.#onMouseMove);

                this.#resetStyles()
            }
        }
    }

    constructor(HTMLElement = document.createElement("input"), isAvoiding = true, radiusPx = 500) {
        this.#isAvoiding = isAvoiding;
        this.radius = radiusPx;
        this.#HTMLElement = HTMLElement;
        if(!this.#HTMLElement.type)
            this.#HTMLElement.type = "button";

        this.#HTMLElement.addEventListener("mouseenter", () => {
            if(this.#isAvoiding && !this.#isRunning){
                const clientRect = this.#HTMLElement.getBoundingClientRect();

                this.#cx = clientRect.x + clientRect.width / 2;
                this.#cy = clientRect.y + clientRect.height / 2;
                document.addEventListener("mousemove", this.#onMouseMove);
            }
            this.#isRunning = this.#isAvoiding;
        });
    }

    get HTMLElement() {
        return this.#HTMLElement;
    }

    get isAvoiding() {
        return this.#isAvoiding;
    }

    set setAvoiding(isAvoiding) {
        this.#isAvoiding = isAvoiding;
        this.#isRunning = this.#isRunning && isAvoiding;
        document.removeEventListener("mousemove", this.#onMouseMove);

        this.#resetStyles();
    }

    #getDistanceVectorToCursor(e){
        const clientRect = this.#HTMLElement.getBoundingClientRect();
        const offset_x = e.clientX - this.#cx;
        const offset_y = e.clientY - this.#cy;
        return {x: offset_x, y: offset_y, length: Math.sqrt(offset_x ** 2 + offset_y ** 2)};
    }

    #resetStyles(){
        this.#HTMLElement.style.translate = "0";
        this.#HTMLElement.style.transform = "none";

        this.#HTMLElement.style.boxShadow = "none";
        this.#HTMLElement.style.border = "1px " + this.borderStyle;
    }
}