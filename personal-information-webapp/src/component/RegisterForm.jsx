import {Button} from "primereact/button";
import {Divider} from "primereact/divider";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {LOGIN_PAGE} from "../constant/page.js";
import {formatBirthday} from "../utils/timeUtils.js";
import {
    CONFIRM_PWD_NOT_MATCH_PWD_ERROR,
    EMPTY_INPUT_ERROR,
    INVALID_LENGTH_ERROR,
    USERNAME_ERROR
} from "../constant/message.js";
import {
    checkContainsCapitalAndLowerLetter,
    checkContainsSymbolAndNumber,
    checkValidLength,
    checkValidUsername
} from "../validation/validator.js";
import {isExistsUsername} from "../api/userApi.js";
import {register} from "../api/authApi.js";
import {getServerErrorMessages} from "../utils/errorUtils.js";
import ErrorMessages from "./ErrorMessages.jsx";
import UsernamePassword from "./UsernamePassword.jsx";
import BirthdayCalendar from "./BirthdayCalendar.jsx";
import PasswordInstructions from "./PasswordInstructions.jsx";
import {Password} from "primereact/password";
import {MAX_LENGTH} from "../constant/number.js";
import {BsInfoCircle} from "react-icons/bs";

function RegisterForm() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [birthday, setBirthday] = useState(null);
    const [usernameError, setUsernameError] = useState("");
    const [showPasswordError, setShowPasswordError] = useState(false);
    const [confirmPasswordError, setConfirmPasswordError] = useState("");
    const [isValidLength, setIsValidLength] = useState(false);
    const [containsSymbolAndNumber, setContainsSymbolAndNumber] = useState(false);
    const [containsCapitalAndLowerLetter, setContainsCapitalAndLowerLetter] = useState(false);
    const [serverMessages, setServerMessages] = useState([]);

    const handleChangeUsername = (username) => {
        setUsername(username);
        setUsernameError(username ? "" : EMPTY_INPUT_ERROR);
    }

    const handleBlurUsername = async (username) => {
        if (isValidUsername(username)) {
            const data = await isExistsUsername(username);
            if (data.result) {
                setUsernameError(data.description);
            }
        }
    }

    const isValidUsername = (username) => {
        if (username) {
            if (checkValidLength(username)) {
                if (checkValidUsername(username)) {
                    return true;
                } else {
                    setUsernameError(USERNAME_ERROR);
                }
            } else {
                setUsernameError(INVALID_LENGTH_ERROR);
            }
        } else {
            setUsernameError(EMPTY_INPUT_ERROR);
        }
        return false;
    }

    const handleChangePassword = (password) => {
        setPassword(password);
        setShowPasswordError(!isValidPassword(password));
    }

    const handleChangeConfirmPassword = (confirmPassword) => {
        setConfirmPassword(confirmPassword);
        setConfirmPasswordError(confirmPassword === password ? "" : CONFIRM_PWD_NOT_MATCH_PWD_ERROR);
    }

    const isValidPassword = (password) => {
        const isValidLength = checkValidLength(password);
        const containsSymbolAndNumber = checkContainsSymbolAndNumber(password);
        const containsCapitalAndLowerLetter = checkContainsCapitalAndLowerLetter(password);
        setIsValidLength(isValidLength);
        setContainsSymbolAndNumber(containsSymbolAndNumber);
        setContainsCapitalAndLowerLetter(containsCapitalAndLowerLetter);
        return isValidLength && containsSymbolAndNumber && containsCapitalAndLowerLetter;
    }

    const handleClickRegister = async () => {
        if (isValidUsername(username) && isValidPassword(password)) {
            if (confirmPassword !== password) {
                setConfirmPasswordError(CONFIRM_PWD_NOT_MATCH_PWD_ERROR);
                return;
            }
            const _birthday = birthday ? formatBirthday(birthday) : null;
            const requestBody = {username, password, birthday: _birthday};
            await register(requestBody)
                .then((response) => {
                    navigate(LOGIN_PAGE, {state: {message: response.data.message}});
                })
                .catch((error) => {
                    console.log(error)
                    setServerMessages(getServerErrorMessages(error.response.data));
                });
        } else {
            setShowPasswordError(true);
        }
    }


    return (
        <div className="card container lg:col-8">
            <div className="flex flex-column md:flex-row">
                <div className="w-full md:w-8 flex flex-column align-items-start justify-content-center gap-3 py-5">
                    <UsernamePassword
                        username={username}
                        password={password}
                        usernameError={usernameError}
                        onChangeUsername={handleChangeUsername}
                        onBlurUsername={handleBlurUsername}
                        onChangePassword={handleChangePassword}
                    />
                    {
                        showPasswordError &&
                        <PasswordInstructions
                            isValidLength={isValidLength}
                            containsSymbolAndNumber={containsSymbolAndNumber}
                            containsCapitalAndLowerLetter={containsCapitalAndLowerLetter}
                        />
                    }
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="cf-pwd" className="w-12rem">Confirm Password</label>
                        <Password
                            inputId="cf-pwd"
                            toggleMask
                            maxLength={MAX_LENGTH}
                            onChange={(e) => handleChangeConfirmPassword(e.target.value)}
                            onBlur={(e) => handleChangeConfirmPassword(e.target.value)}
                            value={confirmPassword}
                            title={INVALID_LENGTH_ERROR}
                        />
                    </div>
                    {
                        confirmPasswordError?.length > 0 &&
                        <div className={"flex justify-content-start"}>
                            <span className={"text-danger"}>
                                <BsInfoCircle className={"me-1 mb-1"}/>{confirmPasswordError}
                            </span>
                        </div>
                    }
                    <div className="flex flex-wrap justify-content-center align-items-center gap-2 mt-3">
                        <label htmlFor="birthday" className="w-12rem">Birthday</label>
                        <BirthdayCalendar
                            inputId={"birthday"}
                            birthday={birthday}
                            onChangeBirthday={(birthday) => setBirthday(birthday)}
                        />
                    </div>
                    {
                        serverMessages.length > 0 &&
                        <ErrorMessages messages={serverMessages}/>
                    }
                    <Button
                        label="Register"
                        icon="pi pi-user-plus"
                        className="w-10rem mx-auto border-round mt-3"
                        onClick={handleClickRegister}
                    />
                </div>
                <div className="w-full md:w-2">
                    <Divider layout="vertical" className="hidden md:flex"><b>OR</b></Divider>
                    <Divider layout="horizontal" className="flex md:hidden" align="center"><b>OR</b></Divider>
                </div>
                <div className="w-full md:w-4 flex flex-column align-items-center justify-content-center py-5">
                    <p>You&apos;ve already got a user account, right?</p>
                    <p>Log in now!</p>
                    <Button
                        onClick={() => navigate(LOGIN_PAGE)}
                        label="Login"
                        icon="pi pi-sign-in"
                        className="w-10rem p-button-success border-round"
                    />
                </div>
            </div>
        </div>
    );
}

export default RegisterForm;