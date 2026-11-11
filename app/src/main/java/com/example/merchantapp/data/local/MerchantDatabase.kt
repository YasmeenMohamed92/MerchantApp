
package com.example.merchantapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        MerchantPersonalInfoEntity::class,
        MerchantRegistrationDetailsEntity::class
    ],
    version = 5,
    exportSchema = false,

)
abstract class MerchantDatabase : RoomDatabase() {

    abstract fun merchantUserDao(): MerchantUserDao

    companion object {

        private fun columnExists(
            db: SupportSQLiteDatabase,
            tableName: String,
            columnName: String
        ): Boolean {
            db.query("PRAGMA table_info(`$tableName`)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")

                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex) == columnName) {
                        return true
                    }
                }
            }

            return false
        }

        private fun tableExists(
            db: SupportSQLiteDatabase,
            tableName: String
        ): Boolean {
            db.query(
                """
                SELECT 1
                FROM sqlite_master
                WHERE type = 'table' AND name = ?
                """.trimIndent(),
                arrayOf(tableName)
            ).use { cursor ->
                return cursor.moveToFirst()
            }
        }

        // Version 1 -> 2
        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS merchant_personal_info (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        fullName TEXT NOT NULL,
                        countryCode TEXT NOT NULL,
                        phoneNumber TEXT NOT NULL,
                        nationalId TEXT NOT NULL,
                        termsAccepted INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_merchant_personal_info_nationalId
                    ON merchant_personal_info(nationalId)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS merchant_registration_details (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        merchantId INTEGER NOT NULL,
                        businessName TEXT NOT NULL,
                        businessCategory TEXT NOT NULL,
                        businessAddress TEXT NOT NULL,
                        businessWebsite TEXT NOT NULL,
                        settlementType TEXT NOT NULL,
                        selectedBank TEXT NOT NULL,
                        ibanNumber TEXT NOT NULL,
                        isBankStatementUploaded INTEGER NOT NULL,
                        cardNumber TEXT NOT NULL,
                        walletNumber TEXT NOT NULL,
                        FOREIGN KEY(merchantId)
                            REFERENCES merchant_personal_info(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_merchant_registration_details_merchantId
                    ON merchant_registration_details(merchantId)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO merchant_personal_info (
                        fullName,
                        countryCode,
                        phoneNumber,
                        nationalId,
                        termsAccepted
                    )
                    SELECT
                        '',
                        '+20',
                        phoneNumber,
                        nationalId,
                        0
                    FROM merchants
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO merchant_registration_details (
                        merchantId,
                        businessName,
                        businessCategory,
                        businessAddress,
                        businessWebsite,
                        settlementType,
                        selectedBank,
                        ibanNumber,
                        isBankStatementUploaded,
                        cardNumber,
                        walletNumber
                    )
                    SELECT
                        personal.id,
                        '',
                        '',
                        '',
                        '',
                        'BANK_ACCOUNT',
                        '',
                        '',
                        0,
                        '',
                        ''
                    FROM merchant_personal_info AS personal
                    INNER JOIN merchants AS old
                        ON old.nationalId = personal.nationalId
                    """.trimIndent()
                )
            }
        }

        // Version 2 -> 3
        val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                val contractColumn =
                    if (
                        columnExists(
                            db,
                            "merchant_registration_details",
                            "isContractDownloaded"
                        )
                    ) {
                        "isContractDownloaded"
                    } else {
                        "0"
                    }

                val bankStatementColumn =
                    if (
                        columnExists(
                            db,
                            "merchant_registration_details",
                            "isBankStatementUploaded"
                        )
                    ) {
                        """
                        CASE
                            WHEN UPPER(
                                REPLACE(TRIM(settlementType), ' ', '_')
                            ) = 'BANK_ACCOUNT'
                            THEN isBankStatementUploaded
                            ELSE NULL
                        END
                        """.trimIndent()
                    } else {
                        "NULL"
                    }

                db.execSQL(
                    """
                    CREATE TABLE merchant_registration_details_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        merchantId INTEGER NOT NULL,
                        businessName TEXT NOT NULL,
                        businessCategory TEXT NOT NULL,
                        businessAddress TEXT NOT NULL,
                        isContractDownloaded INTEGER NOT NULL,
                        businessWebsite TEXT NOT NULL,
                        settlementType TEXT NOT NULL,
                        selectedBank TEXT NOT NULL,
                        ibanNumber TEXT NOT NULL,
                        isBankStatementUploaded INTEGER,
                        cardNumber TEXT NOT NULL,
                        walletNumber TEXT NOT NULL,
                        FOREIGN KEY(merchantId)
                            REFERENCES merchant_personal_info(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO merchant_registration_details_new (
                        id,
                        merchantId,
                        businessName,
                        businessCategory,
                        businessAddress,
                        isContractDownloaded,
                        businessWebsite,
                        settlementType,
                        selectedBank,
                        ibanNumber,
                        isBankStatementUploaded,
                        cardNumber,
                        walletNumber
                    )
                    SELECT
                        id,
                        merchantId,
                        businessName,
                        businessCategory,
                        businessAddress,
                        $contractColumn,
                        businessWebsite,
                        settlementType,
                        selectedBank,
                        ibanNumber,
                        $bankStatementColumn,
                        cardNumber,
                        walletNumber
                    FROM merchant_registration_details
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE merchant_registration_details")

                db.execSQL(
                    """
                    ALTER TABLE merchant_registration_details_new
                    RENAME TO merchant_registration_details
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX
                    index_merchant_registration_details_merchantId
                    ON merchant_registration_details(merchantId)
                    """.trimIndent()
                )
            }
        }

        // Version 3 -> 4
        // Move uploaded bank-statement status into a separate table.
        // This migration is retained for existing version-3 databases.
        val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS merchant_bank_statement_status (
                        merchantId INTEGER NOT NULL PRIMARY KEY,
                        isUploaded INTEGER NOT NULL,
                        FOREIGN KEY(merchantId)
                            REFERENCES merchant_personal_info(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                if (
                    columnExists(
                        db,
                        "merchant_registration_details",
                        "isBankStatementUploaded"
                    )
                ) {
                    db.execSQL(
                        """
                        INSERT OR IGNORE INTO merchant_bank_statement_status (
                            merchantId,
                            isUploaded
                        )
                        SELECT
                            merchantId,
                            1
                        FROM merchant_registration_details
                        WHERE isBankStatementUploaded = 1
                          AND UPPER(
                              REPLACE(TRIM(settlementType), ' ', '_')
                          ) = 'BANK_ACCOUNT'
                        """.trimIndent()
                    )
                }

                val contractColumn =
                    if (
                        columnExists(
                            db,
                            "merchant_registration_details",
                            "isContractDownloaded"
                        )
                    ) {
                        "isContractDownloaded"
                    } else {
                        "0"
                    }

                db.execSQL(
                    """
                    CREATE TABLE merchant_registration_details_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        merchantId INTEGER NOT NULL,
                        businessName TEXT NOT NULL,
                        businessCategory TEXT NOT NULL,
                        businessAddress TEXT NOT NULL,
                        isContractDownloaded INTEGER NOT NULL,
                        businessWebsite TEXT NOT NULL,
                        settlementType TEXT NOT NULL,
                        selectedBank TEXT NOT NULL,
                        ibanNumber TEXT NOT NULL,
                        cardNumber TEXT NOT NULL,
                        walletNumber TEXT NOT NULL,
                        FOREIGN KEY(merchantId)
                            REFERENCES merchant_personal_info(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO merchant_registration_details_new (
                        id,
                        merchantId,
                        businessName,
                        businessCategory,
                        businessAddress,
                        isContractDownloaded,
                        businessWebsite,
                        settlementType,
                        selectedBank,
                        ibanNumber,
                        cardNumber,
                        walletNumber
                    )
                    SELECT
                        id,
                        merchantId,
                        businessName,
                        businessCategory,
                        businessAddress,
                        $contractColumn,
                        businessWebsite,
                        settlementType,
                        selectedBank,
                        ibanNumber,
                        cardNumber,
                        walletNumber
                    FROM merchant_registration_details
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE merchant_registration_details")

                db.execSQL(
                    """
                    ALTER TABLE merchant_registration_details_new
                    RENAME TO merchant_registration_details
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX
                    index_merchant_registration_details_merchantId
                    ON merchant_registration_details(merchantId)
                    """.trimIndent()
                )
            }
        }

        // Version 4 -> 5
        // Restore bank-statement status to merchant_registration_details.
        val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(db: SupportSQLiteDatabase) {

                val contractColumn =
                    if (
                        columnExists(
                            db,
                            "merchant_registration_details",
                            "isContractDownloaded"
                        )
                    ) {
                        "details.isContractDownloaded"
                    } else {
                        "0"
                    }

                val statementTableExists =
                    tableExists(db, "merchant_bank_statement_status")

                val statementJoin =
                    if (statementTableExists) {
                        """
                        LEFT JOIN merchant_bank_statement_status AS statement
                            ON statement.merchantId = details.merchantId
                        """.trimIndent()
                    } else {
                        ""
                    }

                val statementExpression =
                    if (statementTableExists) {
                        """
                        CASE
                            WHEN UPPER(
                                REPLACE(TRIM(details.settlementType), ' ', '_')
                            ) = 'BANK_ACCOUNT'
                            THEN COALESCE(statement.isUploaded, 0)
                            ELSE NULL
                        END
                        """.trimIndent()
                    } else {
                        """
                        CASE
                            WHEN UPPER(
                                REPLACE(TRIM(details.settlementType), ' ', '_')
                            ) = 'BANK_ACCOUNT'
                            THEN 0
                            ELSE NULL
                        END
                        """.trimIndent()
                    }

                db.execSQL(
                    """
                    CREATE TABLE merchant_registration_details_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        merchantId INTEGER NOT NULL,
                        businessName TEXT NOT NULL,
                        businessCategory TEXT NOT NULL,
                        businessAddress TEXT NOT NULL,
                        isContractDownloaded INTEGER NOT NULL,
                        businessWebsite TEXT NOT NULL,
                        settlementType TEXT NOT NULL,
                        selectedBank TEXT NOT NULL,
                        ibanNumber TEXT NOT NULL,
                        isBankStatementUploaded INTEGER,
                        cardNumber TEXT NOT NULL,
                        walletNumber TEXT NOT NULL,
                        FOREIGN KEY(merchantId)
                            REFERENCES merchant_personal_info(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO merchant_registration_details_new (
                        id,
                        merchantId,
                        businessName,
                        businessCategory,
                        businessAddress,
                        isContractDownloaded,
                        businessWebsite,
                        settlementType,
                        selectedBank,
                        ibanNumber,
                        isBankStatementUploaded,
                        cardNumber,
                        walletNumber
                    )
                    SELECT
                        details.id,
                        details.merchantId,
                        details.businessName,
                        details.businessCategory,
                        details.businessAddress,
                        $contractColumn,
                        details.businessWebsite,
                        details.settlementType,
                        details.selectedBank,
                        details.ibanNumber,
                        $statementExpression,
                        details.cardNumber,
                        details.walletNumber
                    FROM merchant_registration_details AS details
                    $statementJoin
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE merchant_registration_details")

                db.execSQL(
                    """
                    ALTER TABLE merchant_registration_details_new
                    RENAME TO merchant_registration_details
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX
                    index_merchant_registration_details_merchantId
                    ON merchant_registration_details(merchantId)
                    """.trimIndent()
                )

                if (statementTableExists) {
                    db.execSQL("DROP TABLE merchant_bank_statement_status")
                }
            }
        }
    }
}
