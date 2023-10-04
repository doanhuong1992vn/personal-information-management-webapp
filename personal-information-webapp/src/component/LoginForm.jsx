import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {Divider} from "primereact/divider";
import {Password} from "primereact/password";
import {useNavigate} from "react-router-dom";
import {REGISTER_PAGE} from "../constant/page.js";

function LoginForm() {
    const navigate = useNavigate();
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
                        <Password
                            inputId="password"
                            toggleMask
                        />
                    </div>
                    <Button
                        label="Login"
                        icon="pi pi-sign-in"
                        className="w-10rem mx-auto border-round mt-3"
                    />
                </div>
                <div className="w-full md:w-2">
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