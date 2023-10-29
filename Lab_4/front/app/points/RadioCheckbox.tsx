import {useEffect, useState} from "react";

export default function RadioCheckbox({values, setValue} : {values: string[] | number[], setValue: (value: number | string) => void}) {
    const [checkedNum, setCheckedNum] = useState<number>(-1);

    useEffect(() => setValue(checkedNum >= 0 ? values[checkedNum] : -1), [checkedNum])

    return (
        <>
            {values.map((value, i) => (
                <label key={i}>
                    {value}
                    <input checked={i === checkedNum} type="checkbox" value={value}
                           onChange={e => e.currentTarget.checked ? setCheckedNum(i) : setCheckedNum(-1)}/>
                </label>
            ))}
        </>
    )
}