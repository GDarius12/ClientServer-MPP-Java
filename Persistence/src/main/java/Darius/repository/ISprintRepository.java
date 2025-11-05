package Darius.repository;

import Darius.domain.Sprint;

public interface ISprintRepository extends IRepository<Integer, Sprint> {
    Sprint SprintSave(Sprint sprint);
}
