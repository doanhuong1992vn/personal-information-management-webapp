import {Button} from "primereact/button";
import {BsPencilSquare} from "react-icons/bs";
import {useState} from "react";
import {getMaxBirthday, getMinBirthday} from "../service/timeService.js";
import {Calendar} from "primereact/calendar";
import {ImCancelCircle} from "react-icons/im";

function UserProfile() {
    const [username, setUsername] = useState("");
    const [birthday, setBirthday] = useState(null);
    const [isEditBirthday, setIsEditBirthday] = useState(false);
    const [newBirthday, setNewBirthday] = useState(null);
    const [lastLogin, setLastLogin] = useState(null);
    const [createTime, setCreateTime] = useState(null);
    return (
        <div className="card mb-4">
            <div className="card-body">
                <div className="row">
                    <div className="col-sm-3">
                        <p className="mb-0">Username</p>
                    </div>
                    <div className="col-sm-9">
                        <p className="text-muted mb-0">{username}</p>
                    </div>
                </div>
                <hr/>
                <div className="row flex align-items-center">
                    <div className="col-sm-3">
                        <p className="mb-0">Birthday</p>
                    </div>
                    <div className="col-sm-9 flex justify-content-between align-items-center">
                        {
                            isEditBirthday
                                ? <>
                                    <Calendar
                                        style={{width: "249px"}}
                                        inputId={"birthday"}
                                        showIcon
                                        minDate={getMinBirthday()}
                                        maxDate={getMaxBirthday()}
                                        value={newBirthday}
                                        onChange={(e) => setNewBirthday(e.value)}
                                    />
                                    <ImCancelCircle
                                        size={30}
                                        color={"#4F46E5"}
                                        onClick={() => setIsEditBirthday(false)}
                                    />
                                </>
                                : <>
                                    <span className="text-muted mb-0">{birthday}</span>
                                    <BsPencilSquare
                                        size={30}
                                        color={"#4F46E5"}
                                        onClick={() => setIsEditBirthday(true)}
                                    />
                                </>
                        }

                    </div>
                </div>
                <hr/>
                <div className="row">
                    <div className="col-sm-3">
                        <p className="mb-0">Last Login</p>
                    </div>
                    <div className="col-sm-9">
                        <p className="text-muted mb-0">{lastLogin}</p>
                    </div>
                </div>
                <hr/>
                <div className="row">
                    <div className="col-sm-3">
                        <p className="mb-0">Create Time</p>
                    </div>
                    <div className="col-sm-9">
                        <p className="text-muted mb-0">{createTime}</p>
                    </div>
                </div>
            </div>
            <div className={"p-card-footer flex justify-content-center"}>
                <Button
                    label="Update"
                    icon="pi pi-cloud-upload"
                    className="w-10rem mx-auto border-round mt-3"
                />
            </div>
        </div>
    );
}

export default UserProfile;