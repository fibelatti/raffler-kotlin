package com.fibelatti.raffler.core.exception

sealed class Failure : Throwable() {
    class GenericFailure : Failure()
    class PersistenceFailure : Failure()

    /**
     * Extend this class for feature specific failures.
     */
    abstract class FeatureFailure : Failure()
}
