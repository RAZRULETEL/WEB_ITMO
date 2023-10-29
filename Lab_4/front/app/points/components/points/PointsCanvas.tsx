import {Point, RawPoint} from "@/app/api/PointsApi";
import {useEffect, useRef, useState} from "react";
import {LabelStore} from "@/app/points/components/StatusLabel";
import {useTokenStore} from "@/app/api/AuthApi";


export default function PointsCanvas({
                                            points,
                                            axes_offset = 50,
                                            angle = 0,
                                            radius = 1,
                                            onClick,
                                            label,
                                            showOnlyRadiusLayer = false,
                                     }: {
                                            points: Point[],
                                            axes_offset?: number,
                                            angle?: number,
                                            radius?: number | null,
                                            onClick?: (point: RawPoint) => void,
                                            label?: LabelStore,
                                            showOnlyRadiusLayer?: boolean
                                    }) {
    const canvasRef = useRef<HTMLCanvasElement>(null)
    const showedValuesRef = useRef<number>(2.25)
    const [ctx, setCtx] = useState<CanvasRenderingContext2D | null>(null)

    const fractionValueThreshold = 3;

    useEffect(() => {
        if (canvasRef.current && !ctx) {
            const context = canvasRef.current.getContext("2d")
            if (!context) return
            drawAxes(context)
            drawPoints(context)
            canvasRef.current.addEventListener("wheel", e => e.preventDefault())
            setCtx(context)
        }
    }, [canvasRef])

    useEffect(() => {
        if (ctx == null) return
        clear(ctx)
        drawAxes(ctx)
        drawPoints(ctx)
    }, [points, radius, showOnlyRadiusLayer])


    function drawPoints(ctx: CanvasRenderingContext2D) {
        const cx = ctx.canvas.width / 2;
        const scaleX = calcPosition(ctx, 1);
        (showOnlyRadiusLayer ? points.filter(e => e.r == radius) : points).forEach(e => {
            ctx.beginPath()
            ctx.fillStyle = e.success ? "#00c905" : "red";
            ctx.arc(cx + scaleX * e.x, cx - scaleX * e.y, 10, 0, Math.PI * 2)
            ctx.fill()
        })

    }


    function drawAxes(ctx: CanvasRenderingContext2D) {
        const width = ctx.canvas.width, height = ctx.canvas.height;
        const cx = width / 2, cy = height / 2;

        ctx.lineWidth = 5;
        const {rgb, rgba} = randomRGBA(0.2, 50);
        ctx.fillStyle = rgba;
        ctx.strokeStyle = rgb;

        if(radius != null) {
            const pos1 = calcPosition(ctx, radius / 2);
            const pos2 = calcPosition(ctx, radius);

            // Rect
            ctx.beginPath();
            ctx.fillRect(cx - pos1, cy, pos1, pos2);

            ctx.moveTo(cx - pos1, cy);
            ctx.lineTo(cx - pos1, cy + pos2);
            ctx.lineTo(cx, cy + pos2);
            ctx.stroke()

            // Triangle
            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.lineTo(cx, cy + pos2);
            ctx.lineTo(cx + pos1, cy);
            ctx.fill()

            ctx.beginPath();
            ctx.moveTo(cx + pos1, cy);
            ctx.lineTo(cx, cy + pos2);
            ctx.stroke()

            // Arc
            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.arc(cx, cy, Math.abs(pos1), Math.PI / 2 - Math.sign(radius) * Math.PI / 2, Math.PI + Math.PI / 2 * Math.sign(radius), true);
            ctx.fill()
            ctx.stroke()
        }

        ctx.lineWidth = 2;
        ctx.fillStyle = "#000";
        ctx.strokeStyle = "#000";
        ctx.font = 40 + "px monospace";

        ctx.strokeText("User: " + useTokenStore.getState().user as string, 10, 50);

        drawGraphValues(ctx);

        drawArrow(ctx, cx - (cx - axes_offset) * Math.cos(angle), cy - (cy - axes_offset) * Math.sin(angle), cx + (cx - axes_offset) * Math.cos(angle), cy + (cy - axes_offset) * Math.sin(angle));//Ox

        drawArrow(ctx, cx - (cx - axes_offset) * Math.sin(angle), cy + (cy - axes_offset) * Math.cos(angle), cx + (cx - axes_offset) * Math.sin(angle), cy - (cy - axes_offset) * Math.cos(angle));//Oy

        ctx.fillText("x", cx + 10 * Math.sin(angle) + (cx - axes_offset + 5) * Math.cos(angle), cy - 10 * Math.cos(angle) + (cy - axes_offset + 5) * Math.sin(angle));

        ctx.fillText("y", cx + 10 * Math.cos(angle) + (cx - axes_offset + 5) * Math.sin(angle), cx - 10 * Math.sin(angle) - (cy - axes_offset + 5) * Math.cos(angle));
    }


    /**
     * Calculate offset from center to graph value with position
     *
     * @param ctx
     * @param position index of required value
     */
    function calcPosition(ctx: CanvasRenderingContext2D, position: number): number {
        const cx = ctx.canvas.width / 2;
        return (cx - axes_offset) * (position) / (showedValuesRef.current);
    }

    function drawGraphValues(ctx: CanvasRenderingContext2D): void {
        const positions: number[] = [];

        for (// @ts-ignore: boolean arithmetic
            let i = 1 - (showedValuesRef.current < fractionValueThreshold) / 2;
            i <= showedValuesRef.current;// @ts-ignore: boolean arithmetic
            i += (1 + (showedValuesRef.current > fractionValueThreshold)) / 2
        ) positions.push(i);
        const positionsCoords: number[] = positions.map(e => calcPosition(ctx, e));
        const cx = ctx.canvas.width / 2, cy = ctx.canvas.height / 2;
        const valSize = 12 * 2;

        ctx.beginPath();
        for (let i = 0; i < 4; i++) {
            const isOx = i < 3 ? 1 - (i % 3) : 0;
            const isOy = i > 0 ? ((i - 1) % 3) - 1 : 0;

            positionsCoords.map((e, i) => {
                ctx.strokeStyle = "#000";
                if (showedValuesRef.current < fractionValueThreshold && showedValuesRef.current > fractionValueThreshold - 1 && positions[i] % 1 != 0)
                    ctx.strokeStyle = `rgba(0, 0, 0, ${fractionValueThreshold - showedValuesRef.current})`;
                ctx.moveTo(cx + (e) * isOx - (Math.abs(isOy) ? valSize / 2 : 0), cy + (e) * isOy - (Math.abs(isOx) ? valSize / 2 : 0));
                ctx.lineTo(cx + (e) * isOx + (Math.abs(isOy) ? valSize / 2 : 0), cy + (e) * isOy + (Math.abs(isOx) ? valSize / 2 : 0));
                ctx.strokeText(positions[i].toString(), cx + (e) * isOx + (Math.abs(isOy) ? 10 : 0), cy + (e) * isOy - (Math.abs(isOx) ? 10 : 0));
            })
            ctx.stroke()
        }
    }

    return (
        <canvas style={{
            backgroundColor: "var(--foreground)",
            width: "500px",
            height: "500px",
            userSelect: "none",
            borderRadius: "15px",
            backdropFilter: "blur(var(--background-blur))"
        }}
                width="1000"
                height="1000"
                ref={canvasRef}
                onWheel={(e) => {
                    showedValuesRef.current = Math.max(Math.sign(e.deltaY) * 0.1 + showedValuesRef.current, 1)
                    if (ctx != null) {
                        clear(ctx)
                        drawAxes(ctx)
                        drawPoints(ctx)
                    }
                }}
                onClick={(e) => {
                    if (label != null) label.setCoordinates(e.clientX, e.clientY)
                    if (radius == null) {
                        if (label != null) {
                            label.updateVisible(true);
                            label.updateText("Radius not set");
                        }
                        return;
                    }
                    if (onClick == null || ctx == null) return;
                    const rect = ctx.canvas.getBoundingClientRect();
                    const point: RawPoint = {
                        x: (e.clientX - rect.left - ctx.canvas.clientWidth / 2) / calcPosition(ctx, 0.5),
                        y: -(e.clientY - rect.top - ctx.canvas.clientHeight / 2) / calcPosition(ctx, 0.5),
                        r: radius,
                        setR: () => {
                        }
                    };
                    console.log(e.clientY, ctx.canvas.offsetTop, point)
                    onClick(point);
                }}/>
    )
}

function drawArrow(ctx: CanvasRenderingContext2D, from_x: number, from_y: number, to_x: number, to_y: number, arrow_size = 10, color = "#000") {
    const angle = Math.atan2(to_y - from_y, to_x - from_x)
    ctx.strokeStyle = color;
    const width = ctx.lineWidth;
    ctx.lineWidth = 3;
    ctx.beginPath();

    ctx.moveTo(from_x, from_y);
    ctx.lineTo(to_x, to_y);

    ctx.lineTo(to_x + arrow_size * Math.sin(angle - Math.PI / 4), to_y - arrow_size * Math.cos(angle - Math.PI / 4));
    ctx.lineTo(to_x, to_y);


    ctx.lineTo(to_x - arrow_size * Math.sin(angle + Math.PI / 4), to_y + arrow_size * Math.cos(angle + Math.PI / 4));
    ctx.lineTo(to_x, to_y);

    ctx.stroke();
    ctx.lineWidth = width;
}

function clear(ctx: CanvasRenderingContext2D) {
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
}

function randomRGBA(alpha: number, darkness = 0): { rgb: string, rgba: string } {
    const r = Math.floor(Math.random() * (256 - darkness));
    const g = Math.floor(Math.random() * (256 - darkness));
    const b = Math.floor(Math.random() * (256 - darkness));
    return {rgb: `rgb(${r}, ${g}, ${b})`, rgba: `rgba(${r}, ${g}, ${b}, ${alpha})`};
}