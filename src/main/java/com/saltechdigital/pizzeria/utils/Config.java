package com.saltechdigital.pizzeria.utils;

import com.saltechdigital.pizzeria.R;

public class Config {
    public static final float DEFAULT_ZOOM = 14.0f;
    public static final String TAG = "JEANPAUL";
    public static final String PAYGATE_API_KEY = "9ef2ffa6-af1d-4176-be4d-b178948e4278";
    public static final String INTENT_EXTRA_URL = "WebViewInAppUrl";
    public static final String NOTIFICATION_TAG = "JEANPAUL";
    public static final int NOTIFICATION_ID = 26;
    public static final String LIB_REPERE_CHARGEMENT = "Chargement";
    public static final String LIB_REPERE_DECHARGEMENT = "Déchargement";

    public static final String STATUT_LIVRAISON_CREATE = "CREATE";
    public static final String STATUT_LIVRAISON_IN_PROGRESS = "PROGRESS";
    public static final String STATUT_LIVRAISON_END = "TERMINATE";

    public static final String STATUT_COMMANDE_CREE = "CREATE";
    public static final String STATUT_COMMANDE_PAYEE = "PAYEE";
    public static final String STATUT_COMMANDE_INPROGRESSS = "PROGRESS";
    public static final String STATUT_COMMANDE_DELIVERY = "LIVRAISON";
    public static final String STATUT_COMMANDE_END = "TERMINATE";
    public static final String STATUT_COMMANDE_CANCEL = "CANCEL";

    public static final String[] orderProcess = {"Submitted", "Payed", "Deliver", "Collected", "Delivery", "Delivered"};
    public static final int[] orderProcessString = {R.string.order_submitted, R.string.order_payment, R.string.order_deliver, R.string.order_collected, R.string.order_delivery, R.string.order_delivered};
    public static final int[] orderProcessActionString = {R.string.order_submitted, R.string.order_payment_action, R.string.order_deliver_string, R.string.order_collected, R.string.order_delivery_action, R.string.order_delivered_action};
    public static final int[] orderProcessDescription = {R.string.order_submitted_desc, R.string.order_payment_desc, R.string.order_deliver_desc, R.string.order_collected_desc, R.string.order_delivery, R.string.order_delivered};
    public static final int[] orderProcessAction = {0, 1, 1, 0, 1, 1};


}
