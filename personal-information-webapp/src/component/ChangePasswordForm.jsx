import {Password} from "primereact/password";
import {Button} from "primereact/button";
import PasswordInstructions from "./PasswordInstructions.jsx";
import {useState} from "react";
import {MAX_LENGTH} from "../constant/number.js";
import {
    CONFIRM_PWD_NOT_MATCH_NEW_PWD_ERROR,
    CONNECTION_ERROR,
    INVALID_LENGTH_ERROR,
    NEW_PWD_SAME_CURRENT_PWD_ERROR
} from "../constant/message.js";
import {
    checkContainsCapitalAndLowerLetter,
    checkContainsSymbolAndNumber,
    checkValidLength
} from "../validation/validator.js";
import {BsInfoCircle} from "react-icons/bs";
import {updatePassword} from "../api/userApi.js";
import {AiOutlineCheck} from "react-icons/ai";

let passwordData = {
    value: "",
    isValidLength: false,
    containsSymbolAndNumber: false,
    containsCapitalAndLowerLetter: false,
    showError: false
}

function ChangePasswordForm() {
    const [currentPassword, setCurrentPassword] = useState(passwordData);
    const [newPassword, setNewPassword] = useState(passwordData);
    const [isDifference, setIsDifference] = useState(true);
    const [confirmPassword, setConfirmPassword] = useState({value: "", error: ""});
    const [result, setResult] = useState(null);


    const handleChangeCurrentPassword = (password) => {
        isValidPassword(password, setCurrentPassword);
    }

    const handleChangeNewPassword = (password) => {
        isValidPassword(password, setNewPassword);
        setIsDifference(password !== currentPassword.value);
    }

    const handleChangeConfirmPassword = (password) => {
        const error = password === newPassword.value ? "" : CONFIRM_PWD_NOT_MATCH_NEW_PWD_ERROR;
        setConfirmPassword({value: password, error});
    }

    const isValidPassword = (password, callback) => {
        const isValidLength = checkValidLength(password);
        const containsSymbolAndNumber = checkContainsSymbolAndNumber(password);
        const containsCapitalAndLowerLetter = checkContainsCapitalAndLowerLetter(password);
        const isValid = isValidLength && containsSymbolAndNumber && containsCapitalAndLowerLetter;
        callback({
            value: password,
            isValidLength,
            containsSymbolAndNumber,
            containsCapitalAndLowerLetter,
            showError: !isValid
        });
        return isValid;
    }

    const isValidForm = () => {
        const isValidCurrentPassword = isValidPassword(currentPassword.value, setCurrentPassword);
        const isValidNewPassword = isValidPassword(newPassword.value, setNewPassword);
        const isValidConfirmPassword = confirmPassword.value === newPassword.value;
        !isValidConfirmPassword && setConfirmPassword({value: confirmPassword.value, error: CONFIRM_PWD_NOT_MATCH_NEW_PWD_ERROR});
        const isDifference = currentPassword.value !== newPassword.value;
        setIsDifference(isDifference);
        return isValidCurrentPassword && isValidNewPassword && isValidConfirmPassword && isDifference;
    }

    const handleClickUpdatePassword = () => {
        if (isValidForm()) {
            updatePassword({currentPassword: currentPassword.value, newPassword: newPassword.value})
                .then(response => {
                    setCurrentPassword(passwordData);
                    setNewPassword(passwordData);
                    setConfirmPassword({value: "", error: ""});
                    setResult(response.data);
                })
                .catch(error => {
                    if (error.response) {
                        setResult(error.response.data);
                    } else {
                        setResult({success: false, message: CONNECTION_ERROR})
                    }

                })
        }
    }

    return (
        <div className="card mb-4">
            <div className="card-body">
                <div className="w-full col-8 flex flex-column align-items-center justify-content-center gap-3 py-5">
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="cur-pwd" className="w-12rem">Current Password</label>
                        <Password
                            inputId="cur-pwd"
                            toggleMask
                            maxLength={MAX_LENGTH}
                            onChange={(e) => handleChangeCurrentPassword(e.target.value)}
                            onBlur={(e) => handleChangeCurrentPassword(e.target.value)}
                            value={currentPassword.value}
                            title={INVALID_LENGTH_ERROR}
                        />
                    </div>
                    {
                        currentPassword.showError &&
                        <PasswordInstructions
                            isValidLength={currentPassword.isValidLength}
                            containsSymbolAndNumber={currentPassword.containsSymbolAndNumber}
                            containsCapitalAndLowerLetter={currentPassword.containsCapitalAndLowerLetter}
                        />
                    }
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="new-pwd" className="w-12rem">New Password</label>
                        <Password
                            inputId="new-pwd"
                            toggleMask
                            maxLength={MAX_LENGTH}
                            onChange={(e) => handleChangeNewPassword(e.target.value)}
                            onBlur={(e) => handleChangeNewPassword(e.target.value)}
                            value={newPassword.value}
                            title={INVALID_LENGTH_ERROR}
                        />
                    </div>
                    {
                        (!isDifference || newPassword.showError) &&
                        <div
                            className={`${isDifference ? "text-success" : "text-danger"} flex align-items-center`}>
                            <div className={"me-1 mb-1"}>
                                {isDifference ? <AiOutlineCheck/> : <BsInfoCircle/>}
                            </div>
                            <span>{NEW_PWD_SAME_CURRENT_PWD_ERROR}</span>
                        </div>
                    }
                    {
                        newPassword.showError &&
                        <PasswordInstructions
                            isValidLength={newPassword.isValidLength}
                            containsSymbolAndNumber={newPassword.containsSymbolAndNumber}
                            containsCapitalAndLowerLetter={newPassword.containsCapitalAndLowerLetter}
                        />
                    }
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="cf-pwd" className="w-12rem">Confirm New Password</label>
                        <Password
                            inputId="cf-pwd"
                            toggleMask
                            maxLength={MAX_LENGTH}
                            onChange={(e) => handleChangeConfirmPassword(e.target.value)}
                            onBlur={(e) => handleChangeConfirmPassword(e.target.value)}
                            value={confirmPassword.value}
                            title={INVALID_LENGTH_ERROR}
                        />
                    </div>
                    {
                        confirmPassword.error.length > 0 &&
                        <div className={"flex justify-content-start"}>
                        <span className={"text-danger"}>
                            <BsInfoCircle className={"me-1 mb-1"}/>
                            {confirmPassword.error}
                        </span>
                        </div>
                    }
                    {
                        result &&
                        <div
                            className={`${result.success ? "text-success" : "text-danger"} flex align-items-center`}>
                            <div className={"me-1 mb-1"}>
                                {result.success ? <AiOutlineCheck/> : <BsInfoCircle/>}
                            </div>
                            <span>{result.message}</span>
                        </div>
                    }
                    <Button
                        label="Update"
                        icon="pi pi-user"
                        className="w-10rem mx-auto border-round mt-3"
                        onClick={handleClickUpdatePassword}
                    />
                </div>
            </div>
        </div>
    );
}

export default ChangePasswordForm;