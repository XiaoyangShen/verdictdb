/*
 *    Copyright 2019 University of Michigan
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.verdictdb.coordinator;

import com.google.common.base.Optional;

import org.verdictdb.VerdictSingleResult;
import org.verdictdb.core.sqlobject.SelectQuery;
import org.verdictdb.core.sqlobject.ConstantColumn;
import org.verdictdb.exception.VerdictDBException;

public class RunWithOriginalChecker {
  private double threshold = -1;

  public RunWithOriginalChecker(SelectQuery selectQuery) throws VerdictDBException {
    Optional<ConstantColumn> coverageThreshold = selectQuery.getCoverageThreshold();
    if (coverageThreshold.isPresent()) {
      threshold = Double.valueOf((String) coverageThreshold.get().getValue());
      if (threshold < 0 || threshold > 1) {
        throw new VerdictDBException("COVERAGE UNDER threshold must be between 0 and 1.");
      }
    }
  }

  public boolean exceedThreshold(VerdictSingleResult result) {
    if (threshold == -1) {
      return false;
    }
    double fraction = result.getMetaData().coveredFraction;
    return fraction >= threshold;
  }
}


