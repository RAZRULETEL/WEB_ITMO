import {useRef, useState} from "react";


/**
 * Generates a rainbow text effect by applying a gradient background to the given text.
 *
 * @param {string} TextTagContainer - The HTML tag container for the text. Default is "span".
 * @param {string} text - The text to apply the rainbow effect to.
 * @param {number} speed - The speed at which the colors transition. Default is 1.
 * @param {number} angle - The angle of the gradient. Default is 30.
 * @return {JSX.Element} The component with the rainbow text effect.
 */
export default function RainbowText({TextTagContainer = "span", text, speed = 1, angle = 30}:
                                        {TextTagContainer?: string, text: string, speed?: number, angle?: number}) {
    const requestRef = useRef<number>(0)
    const [state, setState] = useState<number>(0)
    const RGBGradient = useRef({
        last_speed: 0,
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
        next() {
            this.colors = this.colors.map(color => {
                let r = color[0] + this.last_speed - speed;
                let g = color[1] + this.last_speed - speed;
                let b = color[2] + this.last_speed - speed;

                if (r >= 254 - speed) {
                    if (b >= 1 + speed) {
                        b -= speed;
                    } else if (g <= 255 - speed) {
                        g += speed;
                    }
                }

                if (g >= 255 - speed) {
                    if (r >= 1 + speed) {
                        r -= speed;
                    } else if (b <= 255 - speed) {
                        b += speed;
                    }
                }

                if (b >= 255 - speed) {
                    if (g >= 1 + speed) {
                        g -= speed;
                    } else if (r <= 255 - speed) {
                        r += speed;
                    }
                }

                return [
                    Math.floor(r * 10) / 10,
                    Math.floor(g * 10) / 10,
                    Math.floor(b * 10) / 10
                ];
            });
            this.last_speed = speed;
        },
        buildGradientString: function (offsetColors = 0, reverseOrder = true) {
            let colors = this.colors;
            if (offsetColors !== 0)
                colors = [...colors.slice(offsetColors), ...colors.slice(0, offsetColors)];
            let gradient = "-webkit-linear-gradient(" + angle + "deg,";
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
    });
    setTimeout(() => {
        setState(state + 1); // Trigger re-render
        RGBGradient.current.next();
    }, 20)

    return (// @ts-ignore
        <TextTagContainer style={{
            backgroundImage: RGBGradient.current.buildGradientString(),
            WebkitBackgroundClip: "text",
            WebkitTextFillColor: "transparent"
        }}>{text}</TextTagContainer>
    )
}