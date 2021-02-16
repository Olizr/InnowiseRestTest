package olizarovich.probation.rest.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

import static olizarovich.probation.rest.specifications.SearchOperation.getSimpleOperation;

/**
 * Class for creation search predicate based of SearchCriteria
 *
 * @param <T> Class in witch we searching
 */
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
                return builder.equal(buildPath(criteria.getKey(), root), criteria.getValue());
            case NEGATION:
                return builder.notEqual(buildPath(criteria.getKey(), root), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThanOrEqualTo(
                        buildPath(criteria.getKey(), root), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThanOrEqualTo(
                        buildPath(criteria.getKey(), root), criteria.getValue().toString());
            case LIKE:
                return builder.like(buildPath(criteria.getKey(), root), "%" + criteria.getValue().toString() + "%");
            default:
                return null;
        }
    }

    /**
     * Creates correct base based on key
     *
     * @param key  Search key
     * @param root Root
     * @param <R>  Type of path
     * @return Path with correct path
     */
    private <R> Path<R> buildPath(String key, Root<T> root) {
        String[] keyParts = key.split("\\.");
        Path<R> expression = null;

        for (String i : keyParts) {
            if (expression == null) {
                expression = root.get(i);
            } else {
                expression = expression.get(i);
            }
        }

        return expression;
    }
}
