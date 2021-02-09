package olizarovich.probation.rest.specifications;

import olizarovich.probation.rest.models.Person;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static olizarovich.probation.rest.specifications.SearchOperation.*;

public class SpecificationImplementation<T> implements Specification<T> {
    private SearchCriteria criteria;

    public SpecificationImplementation() {
        super();
    }

    public SpecificationImplementation(final SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        SearchOperation searchOperation = getSimpleOperation(criteria.getOperation());

        if (searchOperation == null) {
            return null;
        }

        switch (searchOperation) {
            case EQUALITY:
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            default:
                return null;
        }
    }
}
