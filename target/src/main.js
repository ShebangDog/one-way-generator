

const useState = (value) => {

}

const notUseState = (value) => {

}

const value = "init"

const [value, setValue] = useState(value)
const handleChange = (event) => {
  setValue(event.target.value)
}

const bindInput = <input value={value} onChange={handleChange} />

const [value, setValue] = useState(value)
const handleChange = (event) => {
  setValue(event.target.value)
}

const bindInput = <input value={value} onChange={handleChange} />

const [value, setValue] = useState(value)
const handleChange = (event) => {
  setValue(event.target.value)
}

const bindInput = <input value={value} onChange={handleChange} />
