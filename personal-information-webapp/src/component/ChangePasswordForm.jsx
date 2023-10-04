import {Password} from "primereact/password";
import {Button} from "primereact/button";

function ChangePasswordForm() {
    return (
        <div className="card mb-4">
            <div className="card-body">
                <div className="w-full flex flex-column align-items-s justify-content-center gap-3 py-5">
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="cur-pwd" className="w-12rem">Current Password</label>
                        <Password
                            inputId="cur-pwd"
                            toggleMask
                        />
                    </div>
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="new-pwd" className="w-12rem">New Password</label>
                        <Password
                            inputId="new-pwd"
                            toggleMask

                        />
                    </div>
                    <div className="flex flex-wrap justify-content-start align-items-center gap-2">
                        <label htmlFor="cf-pwd" className="w-12rem">Confirm New Password</label>
                        <Password
                            inputId="cf-pwd"
                            toggleMask
                        />
                    </div>
                    <Button
                        label="Update"
                        icon="pi pi-user"
                        className="w-10rem mx-auto border-round mt-3"
                    />
                </div>
            </div>
        </div>
    );
}

export default ChangePasswordForm;