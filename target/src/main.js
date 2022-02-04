console.log("loaded")

const initialValue = ""
const useState = (argument) => {

}

const notUseState = (argument) => {

}

const [value, setValue] = useState(initialValue)
const handleChange = (event) => {
  setValue(event.target.value)
}

const BindInput2 = () => <input value={value} onChange={handleChange} />

const [value1, setValue1] = useState(initialValue)
const handleChange1 = (event) => {
  setValue1(event.target.value)
}

const BindInput3 = () => <input value={value1} onChange={handleChange1} />

const Form = () => {

    return (
        <div>
            <BindInput />
            <BindInput1 />
        </div>
    )
}


