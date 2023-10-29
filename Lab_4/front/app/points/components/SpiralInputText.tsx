import {useEffect, useRef, useState} from "react";

/**
 * Renders a spiral input text component.
 *
 * @param {number} size - The size of the component in pixels. Default is 150.
 * @param {number} fontSize - The font size of the text. Default is 20.
 * @param {number} scaleFactor - Determines how far characters are spaced from each other, lower values are closer. Default is 2.5.
 * @param {string | undefined | null} placeholder - The placeholder text for the input.
 * @param {function} onChange - The function to call when the input value changes.
 * @return {JSX.Element} - The rendered spiral input text component.
 */
export default function SpiralInputText({
                                            size = 150,
                                            fontSize = 20,
                                            scaleFactor = 2.5,
                                            placeholder,
                                            onChange
                                        }:
                                            {
                                                size?: number,
                                                fontSize?: number,
                                                scaleFactor?: number,
                                                placeholder?: string | undefined | null ,
                                                onChange?: (value: HTMLInputElement) => void
                                            }) {
    const input = useRef<HTMLInputElement>(null)
    const canvas = useRef<HTMLCanvasElement>(null)
    const [ctx, setCtx] = useState<CanvasRenderingContext2D | null>(null)

    useEffect(() => {
        if (canvas.current) {
            const ctx = canvas.current.getContext('2d')
            if (ctx) {
                ctx.save()
                setCtx(ctx)
            }
        }
    }, [])

    useEffect(() => {
        redrawInput()
    }, [ctx])


    function redrawInput(): void {
        if (!input.current || !ctx || !canvas.current) {
            return;
        }

        const c: HTMLCanvasElement = canvas.current;
        const inp: HTMLInputElement = input.current;
        const isPlaceholder = !inp.value && placeholder;
        const text = isPlaceholder ? placeholder : inp.value;

        ctx.restore();
        ctx.save();
        ctx.clearRect(-c.width / 2, -c.height / 2, c.width * 2, c.height * 2);
        ctx.translate(c.width / 2, c.height / 2);
        ctx.beginPath();
        const primColor = !isPlaceholder ? ctx.fillStyle : "rgb(143,143,143)";
        ctx.font = `${fontSize}px Arial`;

        // @ts-ignore
        const selectionSize = inp.selectionEnd - inp.selectionStart;

        for (let i = 0; i < text.length; i++) {
            ctx.fillStyle = primColor;

            // @ts-ignore
            if (inp.selectionStart - Math.sign(selectionSize) < i && i < inp.selectionEnd) {
                ctx.fillStyle = "rgb(0,95,192)";
                ctx.fillRect(10 * Math.sqrt(i * 2 / scaleFactor) - 2, -fontSize + 2, 14, fontSize);
                ctx.fillStyle = "white";
            }

            // @ts-ignore
            if (inp.selectionStart - 1 === i && i !== text.length - 1) {
                ctx.fillText("|", 10 * Math.sqrt(i * 2 / scaleFactor) + 10, -4);
            }

            ctx.fillText(text[i], 10 * Math.sqrt(i * 2 / scaleFactor), 0);
            ctx.rotate(60 / Math.sqrt(i / scaleFactor) * Math.PI / 180);
        }

        if (inp.selectionStart === text.length) {
            ctx.fillText("|", 10 * Math.sqrt((text.length / scaleFactor) * 2) - 2, -2);
        }
    }

    return (
        <div style={{position: 'relative'}}>
            <input type="text" style={{
                position: 'relative',
                height: size + "px",
                width: size + "px",
                borderRadius: "100%",
            }} ref={input} onChange={e => {onChange && e.currentTarget && onChange(e.currentTarget); redrawInput();}}
                   onSelect={redrawInput} />
            <div style={{
                position: 'absolute',
                top: 0,
                pointerEvents: 'none',
                width: '100%'
            }}>
                <div style={{position: 'relative', pointerEvents: 'none', width: '100%'}}>
                    <canvas ref={canvas}
                            width={input.current && input.current.nextElementSibling ? ((input.current.nextElementSibling.clientWidth) + "px") : "0"}
                            height={input.current && input.current.nextElementSibling ? ((input.current.nextElementSibling.clientWidth) + "px") : "0"}
                            style={{
                                backgroundColor: "white",
                                pointerEvents: "none",
                                borderRadius: "100%"
                            }}></canvas>
                </div>
            </div>
        </div>
    )
}