package java_onboarding_tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import org.iotivity.OCAceConnectionType;
import org.iotivity.OCAcePermissionsMask;
import org.iotivity.OCAceResource;
import org.iotivity.OCAceWildcard;
import org.iotivity.OCCred;
import org.iotivity.OCCredUtil;
import org.iotivity.OCCreds;
import org.iotivity.OCMain;
import org.iotivity.OCObt;
import org.iotivity.OCPki;
import org.iotivity.OCRole;
import org.iotivity.OCSecurityAce;
import org.iotivity.OCStorage;
import org.iotivity.OCUuidUtil;

public class ObtMain {

    /* user input Scanner */
    private static Scanner scanner = new Scanner(System.in);

    /* Constants */
    private static final int MAX_NUM_RESOURCES = 100;

    /* Sets containing discovered owned and un-owned devices */
    public static Set<OCFDeviceInfo> unownedDevices = Collections.synchronizedSet(new LinkedHashSet<OCFDeviceInfo>());
    public static Set<OCFDeviceInfo> ownedDevices = Collections.synchronizedSet(new LinkedHashSet<OCFDeviceInfo>());

    /* Callback handlers */
    private static UnownedDeviceHandler unownedDeviceHandler = new UnownedDeviceHandler();
    private static OwnedDeviceHandler ownedDeviceHandler = new OwnedDeviceHandler();
    private static GenerateRandomPinHandler generateRandomPinHandler = new GenerateRandomPinHandler();
    private static ProvisionCredentialsHandler provisionCredentialsHandler = new ProvisionCredentialsHandler();
    private static ResetDeviceHandler resetDeviceHandler = new ResetDeviceHandler();
    private static ProvisionAce2Handler provisionAce2Handler = new ProvisionAce2Handler();
    private static ProvisionAuthWildcardAceHandler provisionAuthWildcardAceHandler = new ProvisionAuthWildcardAceHandler();
    private static ProvisionRoleWildcardAceHandler provisionRoleWildcardAceHandler = new ProvisionRoleWildcardAceHandler();
    private static ProvisionIdCertificateHandler provisionIdCertificateHandler = new ProvisionIdCertificateHandler();
    private static ProvisionRoleCertificateHandler provisionRoleCertificateHandler = new ProvisionRoleCertificateHandler();

    static private boolean quit;
    static private Thread mainThread;
    static private Thread shutdownHook = new Thread() {
        public void run() {
            quit = true;
            System.out.println("Calling mainShutdown.");
            OCMain.mainShutdown();
            OCObt.shutdown();
            scanner.close();
            mainThread.interrupt();
        }
    };

    private static int getIntUserInput() {
        while(!scanner.hasNextInt()) {
            System.out.println("Invalid input. Integer expected.");
            scanner.nextLine();
        }
        return scanner.nextInt();
    }

    public static void displayMenu() {
        StringBuilder menu = new StringBuilder();
        menu.append("\n################################################\n");
        menu.append("OCF 2.x Onboarding Tool\n");
        menu.append("################################################\n");
        menu.append("[0] Display this menu\n");
        menu.append("------------------------------------------------\n");
        menu.append("[1] Discover un-owned devices\n");
        menu.append("[2] Discover un-owned devices in the realm-local IPv6 scope\n");
        menu.append("[3] Discover un-owned devices in the site-local IPv6 scope\n");
        menu.append("[4] Discover owned devices\n");
        menu.append("[5] Discover owned devices in the realm-local IPv6 scope\n");
        menu.append("[6] Discover owned devices in the site-local IPv6 scope\n");
        menu.append("[7] Discover all resources on the device\n");
        menu.append("------------------------------------------------\n");
        menu.append("[8] Just-Works Ownership Transfer Method\n");
        menu.append("[9] Request Random PIN from device for OTM\n");
        menu.append("[10] Random PIN Ownership Transfer Method\n");
        menu.append("[11] Manufacturer Certificate based Ownership Transfer Method\n");
        menu.append("------------------------------------------------\n");
        menu.append("[12] Provision pair-wise credentials\n");
        menu.append("[13] Provision ACE2\n");
        menu.append("[14] Provision auth-crypt RW access to NCRs\n");
        menu.append("[15] RETRIEVE /oic/sec/cred\n");
        menu.append("[16] DELETE cred by credid\n");
        menu.append("[17] RETRIEVE /oic/sec/acl2\n");
        menu.append("[18] DELETE ace by aceid\n");
        menu.append("[19] RETRIEVE own creds\n");
        menu.append("[20] DELETE own cred by credid\n");
        menu.append("[21] Provision role RW access to NCRs\n");
        menu.append("[22] Provision identity certificate\n");
        menu.append("[23] Provision role certificate\n");
        menu.append("------------------------------------------------\n");
        menu.append("[96] Install new manufacturer trust anchor\n");
        menu.append("[97] RESET device\n");
        menu.append("[98] RESET OBT\n");
        menu.append("------------------------------------------------\n");
        menu.append("[99] Exit\n");
        menu.append("################################################\n");
        menu.append("\nSelect option: ");
        System.out.print(menu);
    }

    private static void discoverUnownedDevices(int scope) {
        if (scope == 0) {
            System.out.println("Discovering un-owned devices.");
            if (0 > OCObt.discoverUnownedDevices(unownedDeviceHandler)) {
                System.err.println("ERROR discovering un-owned Devices.");
            }
        } else if (scope == 1) {
            System.out.println("Discovering un-owned devices realm local IPv6.");
            if (0 > OCObt.discoverUnownedDevicesRealmLocalIPv6(unownedDeviceHandler)) {
                System.err.println("ERROR discovering un-owned Devices realm local IPv6.");
            }
        } else if (scope == 2) {
            System.out.println("Discovering un-owned devices site local IPv6.");
            if (0 > OCObt.discoverUnownedDevicesSiteLocalIPv6(unownedDeviceHandler)) {
                System.err.println("ERROR discovering un-owned Devices site local IPv6.");
            }
        }
    }

    private static void discoverOwnedDevices(int scope) {
        if (scope == 0) {
            System.out.println("Discovering owned devices.");
            if (0 > OCObt.discoverOwnedDevices(ownedDeviceHandler)) {
                System.err.println("ERROR discovering owned Devices.");
            }
        } else if (scope == 1) {
            System.out.println("Discovering owned devices realm local IPv6.");
            if (0 > OCObt.discoverOwnedDevicesRealmLocalIPv6(ownedDeviceHandler)) {
                System.err.println("ERROR discovering owned Devices realm local IPv6.");
            }
        } else if (scope == 2) {
            System.out.println("Discovering owned devices site local IPv6.");
            if (0 > OCObt.discoverOwnedDevicesSiteLocalIPv6(ownedDeviceHandler)) {
                System.err.println("ERROR discovering owned Devices site local IPv6.");
            }
        }
    }

    public static void discoverResources()
    {
        if(unownedDevices.isEmpty() && ownedDevices.isEmpty()) {
            System.out.println("\nPlease Re-discover devices");
            return;
        }

        int i = 0;

        StringBuilder devicesMenu = new StringBuilder();
        devicesMenu.append("\nMy Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        OCFDeviceInfo[] uds = unownedDevices.toArray(new OCFDeviceInfo[unownedDevices.size()]);
        OCFDeviceInfo[] devices = new OCFDeviceInfo[(ods.length + uds.length)];
        System.arraycopy(ods, 0, devices, 0, ods.length);
        System.arraycopy(uds, 0, devices, ods.length, uds.length);
        for (OCFDeviceInfo od : ods) {
            devicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        devicesMenu.append("\n\nUnowned Devices:\n");
        for (OCFDeviceInfo ud : uds) {
            devicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(ud.uuid) + " - " + ud.name + "\n");
            i++;
        }
        devicesMenu.append("\n\nSelect device: ");
        System.out.print(devicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        int ret = OCObt.discoverAllResources(devices[userInput].uuid, new ResourceDiscovery());
        if (ret >= 0)
        {
            System.out.println("\nSuccessfully issued resource discovery request");
        } else {
            System.out.println("\nERROR issuing resource discovery request");
        }
    }

    private static void otmJustWorks() {
        if (unownedDevices.isEmpty()) {
            System.out.println("\nPlease Re-discover Unowned devices");
            return;
        }

        int i = 0;

        StringBuilder unownedDevicesMenu = new StringBuilder();
        unownedDevicesMenu.append("\nUnowned Devices:\n");
        OCFDeviceInfo[] uds = unownedDevices.toArray(new OCFDeviceInfo[unownedDevices.size()]);
        for (OCFDeviceInfo ud : uds) {
            unownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(ud.uuid) + " - " + ud.name + "\n");
            i++;
        }
        unownedDevicesMenu.append("\n\nSelect device: ");
        System.out.print(unownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        JustWorksHandler justWorksHandler = new JustWorksHandler(uds[userInput]);
        int ret = OCObt.performJustWorksOtm(uds[userInput].uuid, justWorksHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to perform ownership transfer");
        } else {
            System.out.println("\nERROR issuing request to perform ownership transfer");
        }

        /*
         * Having issued an OTM request, remove this item from the unowned
         * device list
         */
        unownedDevices.remove(uds[userInput]);
    }

    private static void requestRandomPin() {
        if (unownedDevices.isEmpty()) {
            System.out.println("\nPlease Re-discover Unowned devices");
            return;
        }

        int i = 0;

        StringBuilder unownedDevicesMenu = new StringBuilder();
        unownedDevicesMenu.append("\nUnowned Devices:\n");
        OCFDeviceInfo[] uds = unownedDevices.toArray(new OCFDeviceInfo[unownedDevices.size()]);
        for (OCFDeviceInfo ud : uds) {
            unownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(ud.uuid) + " - " + ud.name + "\n");
            i++;
        }
        unownedDevicesMenu.append("\n\nSelect device: ");
        System.out.print(unownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        int ret = OCObt.requestRandomPin(uds[userInput].uuid, generateRandomPinHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to generate a random pin");
        } else {
            System.out.println("\nERROR issuing request to generate a random pin");
        }
    }

    private static void otmRandomPin() {
        if (unownedDevices.isEmpty()) {
            System.out.println("\nPlease Re-discover Unowned devices");
            return;
        }

        int i = 0;

        StringBuilder unownedDevicesMenu = new StringBuilder();
        unownedDevicesMenu.append("\nUnowned Devices:\n");
        OCFDeviceInfo[] uds = unownedDevices.toArray(new OCFDeviceInfo[unownedDevices.size()]);
        for (OCFDeviceInfo ud : uds) {
            unownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(ud.uuid) + " - " + ud.name + "\n");
            i++;
        }
        unownedDevicesMenu.append("\n\nSelect device: ");
        System.out.print(unownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        System.out.print("Enter Random PIN: ");
        String pin = scanner.next();
        // max string length for pin is 24 characters
        if (pin.length() > 24) {
            pin = pin.substring(0, 24);
        }

        OtmRandomPinHandler otmRandomPinHandler = new OtmRandomPinHandler(uds[userInput]);
        int ret = OCObt.performRandomPinOtm(uds[userInput].uuid, pin, otmRandomPinHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to perform Random PIN OTM");
        } else {
            System.out.println("\nERROR issuing request to perform Random PIN OTM");
        }

        /*
         * Having issued an OTM request, remove this item from the unowned
         * device list
         */
        unownedDevices.remove(uds[userInput]);
    }

    private static void otmCertificate() {
        if (unownedDevices.isEmpty()) {
            System.out.println("\nPlease Re-discover Unowned devices");
            return;
        }

        int i = 0;

        StringBuilder unownedDevicesMenu = new StringBuilder();
        unownedDevicesMenu.append("\nUnowned Devices:\n");
        OCFDeviceInfo[] uds = unownedDevices.toArray(new OCFDeviceInfo[unownedDevices.size()]);
        for (OCFDeviceInfo ud : uds) {
            unownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(ud.uuid) + " - " + ud.name + "\n");
            i++;
        }
        unownedDevicesMenu.append("\n\nSelect device: ");
        System.out.print(unownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        OtmCertificationHandler otmCertificateHandler = new OtmCertificationHandler(uds[userInput]);
        int ret = OCObt.performCertOtm(uds[userInput].uuid, otmCertificateHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to perform Certificate OTM");
        } else {
            System.out.println("\nERROR issuing request to perform Certificate OTM");
        }

        /*
         * Having issued an OTM request, remove this item from the unowned
         * device list
         */
        unownedDevices.remove(uds[userInput]);
    }

    private static void provisionCredentials() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("\nMy Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device 1: ");
        System.out.print(ownedDevicesMenu);
        int userInput1 = getIntUserInput();
        if (userInput1 < 0 || userInput1 >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        System.out.print("\nSelect device 2: ");
        int userInput2 = getIntUserInput();
        if (userInput2 < 0 || userInput2 >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        int ret = OCObt.provisionPairwiseCredentials(ods[userInput1].uuid, ods[userInput2].uuid,
                provisionCredentialsHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to provision credentials");
        } else {
            System.out.println("\nERROR issuing request to provision credentials");
        }
    }

    public static void provisionAce2() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        String[] connTypes = new String[] { "anon-clear", "auth-crypt" };
        int num_resources = 0;

        System.out.println("\nProvision ACE2\nMy Devices:");

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("\nMy Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }

        if (i == 0) {
            System.out.println("\nNo devices to provision... Please Re-Discover Owned devices.");
            return;
        }

        ownedDevicesMenu.append("\n\nSelect device for provisioning: ");
        System.out.print(ownedDevicesMenu);
        int dev = getIntUserInput();
        if (dev < 0 || dev >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        StringBuilder subjectsMenu = new StringBuilder();
        subjectsMenu.append("\nSubjects:\n");
        subjectsMenu.append("[0]: " + connTypes[0] + "\n");
        subjectsMenu.append("[1]: " + connTypes[1] + "\n");
        subjectsMenu.append("[2]: Role\n");
        i = 0;
        for (OCFDeviceInfo od : ods) {
            subjectsMenu.append("[" + (i + 3) + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        subjectsMenu.append("\nSelect subject: ");
        System.out.print(subjectsMenu);
        int sub = getIntUserInput();

        if (sub >= (i + 3)) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        OCSecurityAce ace = null;
        if (sub > 2) {
            ace = OCObt.newAceForSubject(ods[sub - 3].uuid);
        } else {
            if (sub == 0) {
                ace = OCObt.newAceForConnection(OCAceConnectionType.OC_CONN_ANON_CLEAR);
            } else if (sub == 1) {
                ace = OCObt.newAceForConnection(OCAceConnectionType.OC_CONN_AUTH_CRYPT);
            } else {
                System.out.print("\nEnter role: ");
                String role = scanner.next();
                // max string length for role is 64 characters
                if (role.length() > 64) {
                    role = role.substring(0, 64);
                }
                String authority = null;
                System.out.print("Authority? [0-No, 1-Yes]: ");
                int c = getIntUserInput();
                if (c == 1) {
                    System.out.print("\nEnter authority: ");
                    authority = scanner.next();
                    // max string length for role is 64 characters
                    if (authority.length() > 64) {
                        authority = authority.substring(0, 64);
                    }

                }
                ace = OCObt.newAceForRole(role, authority);
            }
        }

        if (ace == null) {
            System.out.println("\nERROR: Could not create ACE");
            return;
        }

        while (num_resources <= 0 || num_resources > MAX_NUM_RESOURCES) {
            if (num_resources != 0) {
                System.out.println("\n\nERROR: Enter valid number\n");
            }
            System.out.print("\nEnter number of resources in this ACE: ");
            num_resources = getIntUserInput();
        }

        System.out.println("\nResource properties");
        i = 0;
        while (i < num_resources) {
            OCAceResource res = OCObt.aceNewResource(ace);

            if (res == null) {
                System.out.println("\nERROR: Could not allocate new resource for ACE");
                OCObt.freeAce(ace);
                return;
            }

            System.out.print("Have resource href? [0-No, 1-Yes]: ");
            int c = getIntUserInput();
            if (c == 1) {
                System.out.print("Enter resource href (eg. /a/light): ");
                String href;
                // max string length in C is 64 characters including
                // the nul character, so useable lenght is 63
                href = scanner.next();
                if (href.length() > 63) {
                    href = href.substring(0, 63);
                }

                OCObt.aceResourceSetHref(res, href);
                OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_NO_WC);
            } else {
                System.out.print("\nSet wildcard resource? [0-No, 1-Yes]: ");
                c = getIntUserInput();
                if (c == 1) {
                    StringBuilder wildcardMenu = new StringBuilder();
                    wildcardMenu.append("[1]: All NCRs '*'\n");
                    wildcardMenu.append("[2]: All NCRs with >=1   secured endpoint '+'\n");
                    wildcardMenu.append("[3]: All NCRs with >=1 unsecured endpoint '-'\n");
                    wildcardMenu.append("\nSelect wildcard resource: ");
                    System.out.print(wildcardMenu);
                    c = getIntUserInput();
                    switch (c) {
                    case 1:
                        OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_WC_ALL);
                        break;
                    case 2:
                        OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_WC_ALL_SECURED);
                        break;
                    case 3:
                        OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_WC_ALL_PUBLIC);
                        break;
                    default:
                        break;
                    }
                }
            }
            i++;
        }

        System.out.println("\nSet ACE2 permissions");
        System.out.print("CREATE [0-No, 1-Yes]: ");
        int c = getIntUserInput();
        if (c == 1) {
            OCObt.aceAddPermission(ace, OCAcePermissionsMask.CREATE);
        }
        System.out.print("RETRIEVE [0-No, 1-Yes]: ");
        c = getIntUserInput();
        if (c == 1) {
            OCObt.aceAddPermission(ace, OCAcePermissionsMask.RETRIEVE);
        }
        System.out.print("UPDATE [0-No, 1-Yes]: ");
        c = getIntUserInput();
        if (c == 1) {
            OCObt.aceAddPermission(ace, OCAcePermissionsMask.UPDATE);
        }
        System.out.print("DELETE [0-No, 1-Yes]: ");
        c = getIntUserInput();
        if (c == 1) {
            OCObt.aceAddPermission(ace, OCAcePermissionsMask.DELETE);
        }
        System.out.print("NOTIFY [0-No, 1-Yes]: ");
        c = getIntUserInput();
        if (c == 1) {
            OCObt.aceAddPermission(ace, OCAcePermissionsMask.NOTIFY);
        }

        int ret = OCObt.provisionAce(ods[dev].uuid, ace, provisionAce2Handler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to provision ACE");
        } else {
            System.out.println("\nERROR issuing request to provision ACE");
        }
    }

    public static void provisionAuthWildcardAce() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nNo devices to provision.. Please Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("\nProvision auth crypt * ACE\n");
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device for provisioning: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }
        int ret = OCObt.provisionAuthWildcardAce(ods[userInput].uuid, provisionAuthWildcardAceHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to provision auth-crypt * ACE");
        } else {
            System.out.println("\nERROR issuing request to provision auth-crypt * ACE");
        }
    }

    public static void displayCredentialResource(OCCreds creds)
    {
        if (creds != null) {
            System.out.println("\n/oic/sec/cred:");
            OCCred cr = creds.getCredsListHead();
            System.out.println("\n################################################");
            while (cr != null) {
              System.out.println("credid: " + cr.getCredId());
              System.out.println("subjectuuid: " + OCUuidUtil.uuidToString(cr.getSubjectUuid()));
              System.out.println("credtype: " + OCCredUtil.credTypeString(cr.getCredType()));
              // In The C sample this section is only for OC_PKI we don't have build flags in Java
              // TODO add empty Get function for Credusage is OC_PKI not supported.
              System.out.println("credusage: " + OCCredUtil.readCredUsage(cr.getCredUsage()));
              if (cr.getPublicData() != null &&
                  cr.getPublicData().getData() != null &&
                  !cr.getPublicData().getData().isEmpty()) {
                System.out.println("publicdata_encoding: " + OCCredUtil.readEncoding((cr.getPublicData().getEncoding())));
              }
              // End of OC_PKI section
              System.out.println("privatedata_encoding: " + OCCredUtil.readEncoding(cr.getPrivateData().getEncoding()));
              if (cr.getRole() != null && !cr.getRole().isEmpty()) {
                  System.out.println("roleid_role: " + cr.getRole());
              }
              if (cr.getAuthority() != null && !cr.getAuthority().isEmpty()) {
                System.out.println("roleid_authority: " + cr.getAuthority());
              }
              System.out.println("\n-----");
              cr = cr.getNext();
            }
            System.out.println("\n################################################");
          }
    }

    public static void retrieveCredentialResource()
    {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        int ret = OCObt.retrieveCreds(ods[userInput].uuid, new RetrieveCredentialResourceHandler());
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to RETRIEVE /oic/sec/cred");
        } else {
            System.out.println("\nERROR issuing request to RETRIEVE /oic/sec/cred");
        }
    }

    public static void deleteCredetialByCredentialId()
    {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        System.out.print("\nEnter credid: ");
        int credid = getIntUserInput();

        int ret = OCObt.deleteCredByCredId(ods[userInput].uuid, credid, new DeleteCredentialIdHandler());
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to DELETE /oic/sec/cred");
        } else {
            System.out.println("\nERROR issuing request to DELETE /oic/sec/cred");
        }
    }

    public static void retrieveAce2Resource()
    {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        int ret = OCObt.retrieveAcl(ods[userInput].uuid, new RetrieveAcl2Handler());
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to RETRIEVE /oic/sec/acl2");
        } else {
            System.out.println("\nERROR issuing request to RETRIEVE /oic/sec/acl2");
        }
    }

    public static void deleteAceByAceId()
    {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        System.out.print("\nEnter aceid: ");
        int aceid = getIntUserInput();

        int ret = OCObt.deleteAceByAceId(ods[userInput].uuid, aceid, new DeleteAceByAceIdHandler());
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to DELETE /oic/sec/acl2");
        } else {
            System.out.println("\nERROR issuing request to DELETE /oic/sec/acl2");
        }
    }

    public static void retrieveOwnCredentials()
    {
        displayCredentialResource(OCObt.retrieveOwnCreds());
    }

    public static void deleteOwnCredentialByCredentialId()
    {
        System.out.print("\nEnter credid: ");
        int credid = getIntUserInput();

        int ret = OCObt.deleteOwnCredByCredId(credid);
        if (ret >= 0) {
          System.out.println("\nSuccessfully DELETED cred");
        } else {
          System.out.println("\nERROR DELETING cred");
        }
    }

    public static void provisionRoleWildcardAce() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nNo devices to provision.. Please Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("\nProvision role * ACE\n");
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device for provisioning: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        System.out.print("\nEnter role: ");
        String role = scanner.next();
        // max string length for role is 64 characters
        if (role.length() > 64) {
            role = role.substring(0, 64);
        }
        String authority = null;
        System.out.print("Authority? [0-No, 1-Yes]: ");
        int c = getIntUserInput();
        if (c == 1) {
            System.out.print("\nEnter authority: ");
            authority = scanner.next();
            // max string length for role is 64 characters
            if (authority.length() > 64) {
                authority = authority.substring(0, 64);
            }
        }
        int ret = OCObt.provisionRoleWildcardAce(ods[userInput].uuid, role, authority, provisionRoleWildcardAceHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to provision role * ACE");
        } else {
            System.out.println("\nERROR issuing request to provision role * ACE");
        }
    }

    public static void provisionIdCertificate() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }
        int ret = OCObt.provisionIdentityCertificate(ods[userInput].uuid, provisionIdCertificateHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to provision identity certificate");
        } else {
            System.out.println("\nERROR issuing request to provision identity certificate");
        }
    }

    public static void provisionRoleCertificate() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nNo devices to provision.. Please Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("\nProvision role * ACE\n");
        ownedDevicesMenu.append("My Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device for provisioning: ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        OCRole roles = null;
        int c;
        do {
            System.out.print("\nEnter role: ");
            String role = scanner.next();
            // max string length for role is 64 characters
            if (role.length() > 64) {
                role = role.substring(0, 64);
            }
            String authority = null;
            System.out.print("Authority? [0-No, 1-Yes]: ");
            c = getIntUserInput();
            if (c == 1) {
                System.out.print("\nEnter authority: ");
                authority = scanner.next();
                // max string length for role is 64 characters
                if (authority.length() > 64) {
                    authority = authority.substring(0, 64);
                }
            }
            roles = OCObt.addRoleId(roles, role, authority);
            System.out.print("\nMore Roles? [0-No, 1-Yes]: ");
            c = getIntUserInput();
        } while (c == 1);
        int ret = OCObt.provisionRoleCertificate(roles, ods[userInput].uuid, provisionRoleCertificateHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to provision role certificate");
        } else {
            System.out.println("\nERROR issuing request to provision role certificate");
        }
    }

    public static void installTrustAnchor()
    {
        System.out.println("\nPaste certificate here, then hit <ENTER> and type \"done\": ");
        ByteArrayOutputStream certBuffer = new ByteArrayOutputStream();
        while(true) {
            String userInput = scanner.next();
            if(userInput.equals("done")) {
                break;
            }
            try {
                certBuffer.write(userInput.getBytes());
            } catch (IOException e) {
                System.out.println("ERROR reading input. No root certificate installed.");
                return;
            }
        }

        int rootCaCredintialId = OCPki.addMfgTrustAnchor(0, certBuffer.toByteArray());
        if(rootCaCredintialId < 0) {
            System.out.println("ERROR installing root certificate.");
        }
    }

    public static void resetDevice() {
        if (ownedDevices.isEmpty()) {
            System.out.println("\n\nPlease Re-Discover Owned devices");
            return;
        }

        int i = 0;

        StringBuilder ownedDevicesMenu = new StringBuilder();
        ownedDevicesMenu.append("\nMy Devices:\n");
        OCFDeviceInfo[] ods = ownedDevices.toArray(new OCFDeviceInfo[ownedDevices.size()]);
        for (OCFDeviceInfo od : ods) {
            ownedDevicesMenu.append("[" + i + "]: " + OCUuidUtil.uuidToString(od.uuid) + " - " + od.name + "\n");
            i++;
        }
        ownedDevicesMenu.append("\nSelect device : ");
        System.out.print(ownedDevicesMenu);

        int userInput = getIntUserInput();
        if (userInput < 0 || userInput >= i) {
            System.out.println("ERROR: Invalid selection");
            return;
        }

        int ret = OCObt.deviceHardReset(ods[userInput].uuid, resetDeviceHandler);
        if (ret >= 0) {
            System.out.println("\nSuccessfully issued request to perform hard RESET");
        } else {
            System.out.println("\nERROR issuing request to perform hard RESET");
        }
    }

    public static void resetOBT() {
        OCMain.reset();
        OCObt.shutdown();
        ownedDevices.clear();
        unownedDevices.clear();
        OCObt.init();
    }

    public static void main(String[] args) {
        quit = false;
        mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        String creds_path = "./onboarding_tool_creds/";
        java.io.File directory = new java.io.File(creds_path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        System.out.println("Storage Config PATH : " + directory.getPath());
        if (0 != OCStorage.storageConfig(directory.getPath())) {
            System.err.println("Failed to setup Storage Config.");
        }

        OCMain.setFactoryPresetsHandler(new FactoryPresetsHandler());
        ObtInitHandler obtHandler = new ObtInitHandler();
        int init_ret = OCMain.mainInit(obtHandler);
        if (init_ret < 0) {
            System.exit(init_ret);
        }

        while (!quit) {
            displayMenu();
            int userInput = 0;
            try {
                userInput = getIntUserInput();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input.");
                userInput = 0;
            }
            switch (userInput) {
            case 0:
                continue;
            case 1:
                discoverUnownedDevices(0);
                break;
            case 2:
                discoverUnownedDevices(1);
                break;
            case 3:
                discoverUnownedDevices(2);
                break;
            case 4:
                discoverOwnedDevices(0);
                break;
            case 5:
                discoverOwnedDevices(1);
                break;
            case 6:
                discoverOwnedDevices(2);
                break;
            case 7:
                discoverResources();
            case 8:
                otmJustWorks();
                break;
            case 9:
                requestRandomPin();
                break;
            case 10:
                otmRandomPin();
                break;
            case 11:
                otmCertificate();
                break;
            case 12:
                provisionCredentials();
                break;
            case 13:
                provisionAce2();
                break;
            case 14:
                provisionAuthWildcardAce();
                break;
            case 15:
                retrieveCredentialResource();
                break;
            case 16:
                deleteCredetialByCredentialId();
                break;
            case 17:
                retrieveAce2Resource();
                break;
            case 18:
                deleteAceByAceId();
                break;
            case 19:
                retrieveOwnCredentials();
                break;
            case 20:
                deleteOwnCredentialByCredentialId();
                break;
            case 21:
                provisionRoleWildcardAce();
                break;
            case 22:
                provisionIdCertificate();
                break;
            case 23:
                provisionRoleCertificate();
                break;
            case 96:
                installTrustAnchor();
                break;
            case 97:
                resetDevice();
                break;
            case 98:
                resetOBT();
                break;
            case 99:
                quit = true;
                break;
            default:
                break;
            }
        }
        OCMain.mainShutdown();
        OCObt.shutdown();
        System.exit(0);
    }
}
