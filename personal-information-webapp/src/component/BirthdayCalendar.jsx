import {getMaxBirthday, getMinBirthday} from "../utils/timeUtils.js";
import {Calendar} from "primereact/calendar";

function BirthdayCalendar({inputId, birthday, onChangeBirthday}) {

    return (
        <Calendar
            dateFormat={"dd/mm/yy"}
            style={{width: "249px"}}
            inputId={inputId}
            showIcon
            minDate={getMinBirthday()}
            maxDate={getMaxBirthday()}
            value={birthday}
            onChange={(e) => onChangeBirthday(e.target.value)}
        />
    );
}

export default BirthdayCalendar;