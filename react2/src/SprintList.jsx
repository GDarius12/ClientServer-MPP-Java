import { useEffect, useState } from 'react';
import { GetAllSprints, DeleteSprint } from './sprintService';

const SprintList = ({ onSelectSprint }) => {
    const [sprints, setSprints] = useState([]);

    useEffect(() => {
        GetAllSprints().then(setSprints);
    }, []);

    const handleDelete = (id) => {
        DeleteSprint(id).then(() => {
            setSprints(sprints.filter(sprint => sprint.id !== id));
        });
    };

    return (
        <div>
            <h2>Sprint List</h2>
            <ul>
                {sprints.map(sprint => (
                    <li key={sprint.id} onClick={() => onSelectSprint(sprint)} style={{ cursor: 'pointer' }}>
                        {sprint.distance} metri
                        <button onClick={(e) => { e.stopPropagation(); handleDelete(sprint.id); }}>Șterge</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default SprintList;
