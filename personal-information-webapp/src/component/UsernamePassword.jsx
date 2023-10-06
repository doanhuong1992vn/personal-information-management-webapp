import {InputText} from "primereact/inputtext";
import {MAX_LENGTH} from "../constant/number.js";
import {BsInfoCircle} from "react-icons/bs";
import {Password} from "primereact/password";
import {INVALID_LENGTH_ERROR, USERNAME_ERROR} from "../constant/message.js";

function UsernamePassword({username, password, usernameError, onChangeUsername, onBlurUsername, onChangePassword}) {

    const handleChangeUsername = (username) => {
        onChangeUsername(username);
    }

    const handleBlurUsername = (username) => {
        onBlurUsername(username);
    }

    const handleChangePassword = (password) => {
        onChangePassword(password);
    }


    return (<>
        <div className="flex flex-wrap justify-content-center align-items-center gap-2">
            <label htmlFor="username" className="w-12rem">Username</label>
            <InputText
                id="username"
                type="text"
                onChange={(e) => handleChangeUsername(e.target.value)}
                onBlur={(e) => handleBlurUsername(e.target.value)}
                maxLength={MAX_LENGTH}
                value={username}
                title={`${INVALID_LENGTH_ERROR} ${USERNAME_ERROR}`}
            />
        </div>
        {
            usernameError?.length > 0 &&
            <div className={"flex justify-content-start"}>
                <span className={"text-danger"}>
                    <BsInfoCircle className={"me-1 mb-1"}/>{usernameError}
                </span>
            </div>
        }
        <div className="flex flex-wrap justify-content-center align-items-center gap-2">
            <label htmlFor="password" className="w-12rem">Password</label>
            <Password
                inputId="password"
                toggleMask
                maxLength={MAX_LENGTH}
                onChange={(e) => handleChangePassword(e.target.value)}
                onBlur={(e) => handleChangePassword(e.target.value)}
                value={password}
                title={INVALID_LENGTH_ERROR}
            />
        </div>
    </>);
}

export default UsernamePassword;