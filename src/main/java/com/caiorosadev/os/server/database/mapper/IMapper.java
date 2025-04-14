package com.caiorosadev.os.server.database.mapper;

public interface IMapper<Domain, Persistence> {
    Domain toDomain(Persistence persistenceValue);
    Persistence toPersistence(Domain domainValue);
}
