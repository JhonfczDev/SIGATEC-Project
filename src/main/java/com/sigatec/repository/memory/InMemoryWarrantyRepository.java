package com.sigatec.repository.memory;

import java.util.List;

import com.sigatec.model.Warranty;
import com.sigatec.repository.SequentialIdGenerator;
import com.sigatec.repository.WarrantyRepository;

public class InMemoryWarrantyRepository extends InMemoryRepository<Warranty, String>
        implements WarrantyRepository {

    private final SequentialIdGenerator idGenerator = new SequentialIdGenerator("GAR-");

    @Override
    public String nextId() {
        return idGenerator.next();
    }

    /** Also makes the id sequence continue after any stored id, so new ids never collide. */
    @Override
    protected void register(Warranty warranty) {
        super.register(warranty);
        idGenerator.observe(warranty.getId());
    }

    @Override
    public List<Warranty> findByProductSerial(String serialNumber) {
        return findWhere(warranty -> warranty.coversProductWithSerial(serialNumber));
    }

    @Override
    public List<Warranty> findByCustomerDocument(String documentNumber) {
        return findWhere(warranty -> warranty.belongsToCustomer(documentNumber));
    }
}
