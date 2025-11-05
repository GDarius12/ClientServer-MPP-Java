import SprintList from './SprintList'
import SprintForm from './SprintForm.jsx';
import { useState } from 'react';

const App = () => {
    const [selectedSprint, setSelectedSprint] = useState(null);
    const [refresh, setRefresh] = useState(false);

    return (
        <div>
            <h1>Sprinturi</h1>
            <SprintForm sprint={selectedSprint} refreshList={() => { setSelectedSprint(null); setRefresh(!refresh); }} />
            <SprintList key={refresh} onSelectSprint={setSelectedSprint} />
        </div>
    );
};

export default App;
