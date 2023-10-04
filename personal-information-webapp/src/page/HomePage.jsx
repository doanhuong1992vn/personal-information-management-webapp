import {TabPanel, TabView} from "primereact/tabview";
import UserProfile from "../component/UserProfile.jsx";
import ChangePasswordForm from "../component/ChangePasswordForm.jsx";
import {Button} from "primereact/button";
import {useNavigate} from "react-router-dom";
import {LOGIN_PAGE} from "../constant/page.js";

function HomePage() {
    const navigate = useNavigate();

    const handleClickLogout = () => {
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
                    <UserProfile />
                </TabPanel>
                <TabPanel header="Change Password">
                    <ChangePasswordForm />
                </TabPanel>
            </TabView>
        </div>
    );
}

export default HomePage;