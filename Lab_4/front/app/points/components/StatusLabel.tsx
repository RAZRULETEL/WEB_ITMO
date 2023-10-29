import "../css/StatusLabel.css"
import {useEffect, useRef} from "react";

export interface LabelStore {
    text: string,
    x: number,
    y: number,
    visible: boolean
    updateVisible: (visible: boolean) => void
    setCoordinates: (x: number, y: number) => void
    updateText: (text: string) => void
}

export default function Label({label}: {label: LabelStore}) {
    const thisRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if(thisRef.current != null){
            thisRef.current.style.left = (label.x - thisRef.current.clientWidth / 2) + "px"
            thisRef.current.style.top = (label.y - thisRef.current.clientHeight - 5) + "px"
        }
    }, [label])

    return (
        <div
            ref={thisRef}
            style={{
            display: label.visible ? 'block' : 'none'
        }} className="status-label">
            {label.text}
        </div>
    )
}