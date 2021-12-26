const initialValue = ""
const useState = (argument) => {

}

const notUseState = (argument) => {

}

const [value, setValue] = useState(initialValue)
const handleChange = (event) => {
  setValue(event.target.value)
}

const bindInput = <input value={value} onChange={handleChange} />

const [value1, setValue1] = useState(initialValue)
const handleChange1 = (event) => {
  setValue1(event.target.value1)
}

const bindInput1 = <input value={value1} onChange={handleChange1} />

