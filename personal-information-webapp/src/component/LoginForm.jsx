import {Button} from "primereact/button";
import {Divider} from "primereact/divider";
import {useLocation, useNavigate} from "react-router-dom";
import {HOME_PAGE, REGISTER_PAGE} from "../constant/page.js";
import {CONNECTION_ERROR, EMPTY_INPUT_ERROR} from "../constant/message.js";
import {useState} from "react";
import UsernamePassword from "./UsernamePassword.jsx";
import {BsInfoCircle} from "react-icons/bs";
import {login} from "../api/authApi.js";
import {saveUser} from "../service/userService.js";
import {getServerErrorMessages} from "../utils/errorUtils.js";
import ErrorMessages from "./ErrorMessages.jsx";

function LoginForm() {
    const navigate = useNavigate();
    const location = useLocation();
    const message = location.state?.message;
    const [username, setUsername] = useState("webGLsoft2023");
    const [usernameError, setUsernameError] = useState("");
    const [password, setPassword] = useState("WebGlsoft@2023");
    const [passwordError, setPasswordError] = useState("");
    const [serverMessages, setServerMessages] = useState([]);

    const handleChangeUsername = (username) => {
        setUsername(username);
        checkUsername(username);
    }

    const checkUsername = (username) => {
        if (username) {
            setUsernameError("");
            return true;
        }
        setUsernameError(EMPTY_INPUT_ERROR);
        return false;
    }

    const handleChangePassword = (password) => {
        setPassword(password);
        checkPassword(password);
    }

    const checkPassword = (password) => {
        if (password) {
            setPasswordError("");
            return true;
        }
        setPasswordError(EMPTY_INPUT_ERROR);
        return false;
    }

    const isValidForm = () => {
        const isValidUsername = checkUsername(username);
        const isValidPassword = checkPassword(password);
        return isValidUsername && isValidPassword;
    }

    const handleClickLogin = async () => {
        if (isValidForm()) {
            await login({username, password})
                .then((response) => {
                    saveUser(response.data.data);
                    navigate(HOME_PAGE);
                })
                .catch((error) => {
                    if (error.response) {
                        setServerMessages(getServerErrorMessages(error.response.data));
                    } else {
                        setServerMessages([CONNECTION_ERROR])
                    }
                });
        }
    }
    
    const handleKeyDown = async (e) => {
        if (e.key === 'Enter') {
            await handleClickLogin();
        }
    }


    return (
        <div className="card container lg:col-8" onKeyDown={handleKeyDown} tabIndex="0">
            <div className="flex flex-column md:flex-row">
                <div className="w-full md:w-6 flex flex-column align-items-s justify-content-center gap-3 py-5">
                    {
                        message?.length > 0 &&
                        <div className="flex justify-content-center align-items-center gap-2 text-success">
                            <span>{message}</span>
                        </div>
                    }
                    <UsernamePassword
                        username={username}
                        password={password}
                        usernameError={usernameError}
                        onChangeUsername={handleChangeUsername}
                        onBlurUsername={handleChangeUsername}
                        onChangePassword={handleChangePassword}
                    />
                    <div className={"flex justify-content-start"}>
                        <span className={"text-danger"}>
                            {passwordError ? <BsInfoCircle className={"me-1 mb-1"}/> : <></>}
                            {passwordError}
                        </span>
                    </div>
                    {
                        serverMessages.length > 0 &&
                        <ErrorMessages messages={serverMessages} />
                    }
                    <Button
                        label="Login"
                        icon="pi pi-sign-in"
                        className="w-10rem mx-auto border-round mt-3"
                        onClick={handleClickLogin}
                    />
                </div>
                <div className="w-full md:w-1">
                    <Divider layout="vertical" className="hidden md:flex"><b>OR</b></Divider>
                    <Divider layout="horizontal" className="flex md:hidden" align="center"><b>OR</b></Divider>
                </div>
                <div className="w-full md:w-5 flex flex-column align-items-center justify-content-center py-5">
                    <p>Do you not have a user account?</p>
                    <Button
                        onClick={() => navigate(REGISTER_PAGE)}
                        label="Register"
                        icon="pi pi-user-plus"
                        className="p-button-success border-round w-10rem"
                    />
                </div>
            </div>
        </div>
    );
}

export default LoginForm;