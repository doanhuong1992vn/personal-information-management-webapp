import {TabPanel, TabView} from "primereact/tabview";
import UserProfile from "../component/UserProfile.jsx";
import ChangePasswordForm from "../component/ChangePasswordForm.jsx";
import {Button} from "primereact/button";
import {useNavigate} from "react-router-dom";
import {LOGIN_PAGE} from "../constant/page.js";
import {useEffect, useState} from "react";
import {getLastLogin, getUser, removeUser} from "../service/userService.js";
import {getUserInformation} from "../api/userApi.js";
import {getServerErrorMessages} from "../utils/errorUtils.js";
import {logout} from "../api/authApi.js";
import {CONNECTION_ERROR} from "../constant/message.js";

function HomePage() {
    const navigate = useNavigate();

    const [profile, setProfile] = useState({});

    useEffect(() => {
        if (getUser()) {
            getUserInformation()
                .then(response => {
                    let user = response.data.data;
                    const username = user.username;
                    const birthday = user.birthday ;
                    const createTime = user.createTime;
                    const lastLogin = getLastLogin();
                    setProfile({username, birthday, createTime, lastLogin});
                })
                .catch((error) => {
                    removeUser();
                    const messages = error.response
                        ? getServerErrorMessages(error.response.data).join(" ")
                        : CONNECTION_ERROR;
                    navigate(LOGIN_PAGE, { state: { message: messages + " Please login again!" } });
                });
        } else {
            navigate(LOGIN_PAGE);
        }
    }, []);


    const handleUpdateProfile = (profile) => {
        setProfile({...profile, lastLogin: getLastLogin()});
    }


    const handleClickLogout = async () => {
        await logout();
        removeUser();
        navigate(LOGIN_PAGE);
    }


    return (
        <div className="card container lg:col-8">
            <div className={"d-flex justify-content-end"}>
                <Button
                    label="Logout"
                    icon="pi pi-sign-out"
                    className="w-8rem border-round"
                    onClick={handleClickLogout}
                />
            </div>
            <TabView className={"m-0"}>
                <TabPanel  header="User Profile">
                    <UserProfile profile={profile} onUpdateProfile={handleUpdateProfile}/>
                </TabPanel>
                <TabPanel header="Change Password">
                    <ChangePasswordForm />
                </TabPanel>
            </TabView>
        </div>
    );
}

export default HomePage;