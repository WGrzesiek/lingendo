package com.learnwords.deckservice.enums;


import java.io.Serializable;

/**
 * Widoczność talii.
 * <p>
 * PRIVATE - tylko właściciel ma dostęp (domyślna)
 * PUBLIC - każdy może zobaczyć talię
 * </p>
 * <p>
 * Udostępnianie dla konkretnych osób/grup obsługiwane jest przez DeckShare.
 * </p>
 *
 * @since 2.00
 */
public enum DeckVisibility implements Serializable {
    PRIVATE,
    PUBLIC
}
