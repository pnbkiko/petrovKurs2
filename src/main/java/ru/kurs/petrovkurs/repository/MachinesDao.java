package ru.kurs.petrovkurs.repository;

import ru.kurs.petrovkurs.model.Machines;

public class MachinesDao extends BaseDao<Machines> {
    public MachinesDao() {
        super(Machines.class);
    }
}