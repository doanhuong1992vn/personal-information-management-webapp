import {Button} from "primereact/button";
import {BsPencilSquare} from "react-icons/bs";
import {useState} from "react";
import {ImCancelCircle} from "react-icons/im";
import BirthdayCalendar from "./BirthdayCalendar.jsx";
import {updateProfile} from "../api/userApi.js";
import {getServerErrorMessages} from "../utils/errorUtils.js";
import ErrorMessages from "./ErrorMessages.jsx";
import {formatBirthday, parseBirthday} from "../utils/timeUtils.js";

function UserProfile({profile, onUpdateProfile}) {
    const [isEditBirthday, setIsEditBirthday] = useState(false);
    const [newBirthday, setNewBirthday] = useState(new Date());
    const [serverMessages, setServerMessages] = useState([]);
    const [haveChange, setHaveChange] = useState(false);


    const handleClickUpdate = () => {
        if (haveChange) {
            const requestBody = {birthday: formatBirthday(newBirthday)};
            updateProfile(requestBody)
                .then(response => {
                    setIsEditBirthday(false);
                    onUpdateProfile(response.data.data);
                })
                .catch(error => {setServerMessages(getServerErrorMessages(error.response.data));});
        }
    }

    const handleClickSetBirthday = () => {
        const birthday = parseBirthday(profile?.birthday);
        if (birthday  === null) {
            setNewBirthday(new Date());
        } else {
            setNewBirthday(birthday);
        }
        setIsEditBirthday(true);
    }

    const handleChangeBirthday = (birthday) => {
        console.log(birthday)
        setNewBirthday(birthday);
        setHaveChange(true);
    }


return (
    <div className="card mb-4">
        <div className="card-body">
            <div className="row">
                <div className="col-sm-3">
                    <p className="mb-0">Username</p>
                </div>
                <div className="col-sm-9">
                    <p className="text-muted mb-0">{profile?.username}</p>
                </div>
            </div>
            <hr/>
            <div className="row">
                <div className="col-sm-3">
                    <p className="mb-0">Create Time</p>
                </div>
                <div className="col-sm-9">
                    <p className="text-muted mb-0">{profile?.createTime}</p>
                </div>
            </div>
            <hr/>
            <div className="row">
                <div className="col-sm-3">
                    <p className="mb-0">Last Login</p>
                </div>
                <div className="col-sm-9">
                    <p className="text-muted mb-0">{profile?.lastLogin || "This is your first login!"}</p>
                </div>
            </div>
            <hr/>
            <div className="row flex align-items-center" style={{minHeight: "3.2rem"}}>
                <div className="col-sm-3">
                    <p className="mb-0">Birthday</p>
                </div>
                <div className="col-sm-9 flex justify-content-between align-items-center">
                    {
                        isEditBirthday
                            ? <>
                                <BirthdayCalendar
                                    inputId={"birthday"}
                                    birthday={newBirthday}
                                    onChangeBirthday={handleChangeBirthday}
                                />
                                <ImCancelCircle
                                    size={30}
                                    color={"#4F46E5"}
                                    onClick={() => setIsEditBirthday(false)}
                                />
                            </>
                            : <>
                                <span className="text-muted mb-0">{profile?.birthday || "Please update your date of birth!"}</span>
                                <BsPencilSquare
                                    size={30}
                                    color={"#4F46E5"}
                                    onClick={handleClickSetBirthday}
                                />
                            </>
                    }

                </div>
            </div>
            <hr/>
            <div className={"row"}>
                {
                    serverMessages.length > 0 &&
                    <ErrorMessages messages={serverMessages}/>
                }
            </div>
        </div>
        <div className={"p-card-footer flex justify-content-center"}>
            <Button
                label="Update"
                icon="pi pi-cloud-upload"
                className="w-10rem mx-auto border-round mt-3"
                disabled={!newBirthday || !isEditBirthday}
                onClick={handleClickUpdate}
            />
        </div>
    </div>
);
}

export default UserProfile;