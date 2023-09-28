class RGBGradient {
    constructor(speed = 2, angle = 30) {
        this.speed = speed;
        this.last_speed = 0;
        this.degree = angle;
        this.colors = [[255, 0, 0], [255, 125, 0], [255, 255, 0], [125, 255, 0], [0, 255, 0], [0, 255, 125], [0, 255, 255], [0, 125, 255], [0, 0, 255], [125, 0, 255], [255, 0, 255], [255, 0, 125],];
    }

    next() {
        const speed = this.speed;
        for (let i = 0; i < this.colors.length; i++) {
            let r = this.colors[i][0] + this.last_speed - speed;
            let g = this.colors[i][1] + this.last_speed - speed;
            let b = this.colors[i][2] + this.last_speed - speed;
            if (r >= 254 - speed) {
                if (b >= 1 + speed) b -= speed; else if (g <= 255 - speed) g += speed;
            }
            if (g >= 255 - speed) {
                if (r >= 1 + speed) r -= speed; else if (b <= 255 - speed) b += speed;
            }
            if (b >= 255 - speed) {
                if (g >= 1 + speed) g -= speed; else if (r <= 255 - speed) r += speed;
            }
            this.colors[i][0] = Math.floor(r * 10) / 10;
            this.colors[i][1] = Math.floor(g * 10) / 10;
            this.colors[i][2] = Math.floor(b * 10) / 10
        }
        this.last_speed = speed;
    }

    function

    buildGradientString(offsetColors = 0, reverseOrder = true) {
        let colors = this.colors;
        if (offsetColors !== 0) colors = [...colors.slice(offsetColors), ...colors.slice(0, offsetColors)];
        let gradient = "-webkit-linear-gradient(" + this.degree + "deg,";
        if (reverseOrder) {
            for (let i = colors.length - 1; i > 0; i--) {
                if (i !== colors.length - 1) gradient += ","
                gradient += "rgb(" + colors[i].toString() + ")"
            }
            gradient += ",rgb(" + colors[colors.length - 1].toString() + "))";
        } else {
            for (let i = 0; i < colors.length; i++) {
                if (i !== 0) gradient += ","
                gradient += "rgb(" + colors[i].toString() + ")"
            }
            gradient += ",rgb(" + colors[0].toString() + "))";
        }
        return gradient;
    }

    paintChildrenTextRGB(parent) {
        const children = parent.children;
        for (let i = 0; i < children.length; i++) {
            children[i].style.background = this.buildGradientString(i);
            children[i].style["-webkit-background-clip"] = "text";
            children[i].style["-webkit-text-fill-color"] = "transparent";
        }
        this.next();
    }
}