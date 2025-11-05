import { useState, useEffect } from 'react';
import { CreateSprint, UpdateSprint } from './sprintService';

const SprintForm = ({ sprint, refreshList }) => {
    const [formData, setFormData] = useState({ distance: '' });

    useEffect(() => {
        if (sprint) {
            setFormData({ distance: sprint.distance });
        }
    }, [sprint]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (sprint?.id) {
            await UpdateSprint(sprint.id, formData);
        } else {
            await CreateSprint(formData);
        }
        refreshList();
    };

    return (
        <form onSubmit={handleSubmit}>
            <input type="number" name="distance" value={formData.distance} onChange={handleChange} placeholder="Distanta(in metri)" />
            <button type="submit">{sprint ? 'Modify' : 'Add'}</button>
        </form>
    );
};

export default SprintForm;
