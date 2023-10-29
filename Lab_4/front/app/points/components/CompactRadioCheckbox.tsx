import {useEffect, useRef, useState} from "react";

/**
 * Renders a compact radio checkbox component.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.values - The array of values.
 * @param {Function} props.setValue - The function to set the value.
 * @param {string} [props.className] - The optional class name.
 * @return {JSX.Element} The rendered compact radio checkbox component.
 */
export default function CompactRadioCheckbox<T>({
                                                    values,
                                                    setValue,
                                                    className = undefined
                                                }: {
    values: T[];
    setValue: (value: T | null) => void;
    className?: string | undefined;
}) {
    const [checkedIndex, setCheckedIndex] = useState<number>(-1);
    const [lastCheckedIndex, setLastCheckedIndex] = useState<number>(
        (values.length - values.length % 2) / 2
    );

    useEffect(() => {
        setValue(checkedIndex >= 0 ? values[checkedIndex] : null);
    }, [checkedIndex]);

    return (
        <>
            {values
                .slice(lastCheckedIndex - 1, lastCheckedIndex + 2)
                .map((value, index) => (
                    <label key={index} className={className ? className : ""}>
                        {JSON.stringify(value)}
                        <input
                            checked={values.findIndex((e) => value === e) === checkedIndex}
                            type="checkbox"
                            value={JSON.stringify(value)}
                            onChange={(e) => {
                                const currentIndex = values.findIndex((e) => value === e);
                                if (e.currentTarget.checked) setCheckedIndex(currentIndex);
                                else setCheckedIndex(-1);
                                setLastCheckedIndex(
                                    Math.max(1, Math.min(values.length - 2, currentIndex))
                                );
                            }}
                        />
                    </label>
                ))}
        </>
    );
}