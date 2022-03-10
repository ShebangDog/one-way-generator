import {useState} from "../plain";

const createInputFeature = (initialValue) => {
    const [value, renderUI] = createFeature("")

    const handleChange = (event) => {
        renderUI(event.target.value)
    }

    return [
        value,
        handleChange,
    ]
}
const createFeature = useState

export const Form = () => {
    const [value0, handleChange0] = createInputFeature("")
    const [value1, handleChange1] = createInputFeature("")
    const [value2, handleChange2] = createInputFeature("")
    return (
        <div>
            <input value={value2} onChange={handleChange2}/>
            <input value={value1} onChange={handleChange1}/>
            <input value={value0} onChange={handleChange0}/>
        </div>
    )
}
