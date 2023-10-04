import {InputText} from "primereact/inputtext";
import {Password} from "primereact/password";
import {Calendar} from "primereact/calendar";
import {Button} from "primereact/button";
import {Divider} from "primereact/divider";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {LOGIN_PAGE} from "../constant/page.js";
import {getMaxBirthday, getMinBirthday} from "../service/timeService.js";

function RegisterForm() {
    const navigate = useNavigate();
    const [birthday, setBirthday] = useState(null);

    return (
        <div className="card container lg:col-8">
            <div className="flex flex-column md:flex-row">
                <div className="w-full md:w-5 flex flex-column align-items-s justify-content-center gap-3 py-5">
                    <div className="flex flex-wrap justify-content-center align-items-center gap-2">
                        <label htmlFor="username" className="w-6rem">Username</label>
                        <InputText
                            id="username"
                            type="text"
                        />
                    </div>
                    <div className="flex flex-wrap justify-content-center align-items-center gap-2">
                        <label htmlFor="password" className="w-6rem">Password</label>
                        <Password inputId="password" toggleMask/>
                    </div>
                    <div className="flex flex-wrap justify-content-center align-items-center gap-2">
                        <label htmlFor="birthday" className="w-6rem">Birthday</label>
                        <Calendar
                            style={{width: "249px"}}
                            inputId={"birthday"}
                            showIcon
                            minDate={getMinBirthday()}
                            maxDate={getMaxBirthday()}
                            value={birthday}
                            onChange={(e) => setBirthday(e.value)}
                        />
                    </div>
                    <Button
                        label="Register"
                        icon="pi pi-user-plus"
                        className="w-10rem mx-auto border-round mt-3"
                    />
                </div>
                <div className="w-full md:w-2">
                    <Divider layout="vertical" className="hidden md:flex"><b>OR</b></Divider>
                    <Divider layout="horizontal" className="flex md:hidden" align="center"><b>OR</b></Divider>
                </div>
                <div className="w-full md:w-5 flex flex-column align-items-center justify-content-center py-5">
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