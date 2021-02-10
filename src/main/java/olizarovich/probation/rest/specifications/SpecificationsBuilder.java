package olizarovich.probation.rest.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to build specifications to search with repository
 * @param <T> Class specification build for
 */
public class SpecificationsBuilder<T> {
    /**
     * Contains criteria for search
     */
    private final List<SearchCriteria> params;

    public SpecificationsBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    /**
     * Add to criteria in builder
     * @param key What we are searching for. Foreign key separated by dot. Example "personAddress.Id"
     * @param operation Rules of searching
     * @param value Value to compare with
     * @return SpecificationsBuilder with added criteria
     */
    public SpecificationsBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    /**
     * Creates specification using all criteria in list
     * @return Specification with all criteria
     */
    public Specification<T> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(SpecificationImplementation::new)
                .collect(Collectors.toList());

        Specification<T> result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result)
                    .and(specs.get(i));
        }

        return result;
    }
}
